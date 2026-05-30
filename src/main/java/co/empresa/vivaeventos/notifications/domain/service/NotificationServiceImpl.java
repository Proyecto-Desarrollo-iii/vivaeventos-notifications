package co.empresa.vivaeventos.notifications.domain.service;

import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationResponseDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateResponseDto;
import co.empresa.vivaeventos.notifications.domain.model.Notification;
import co.empresa.vivaeventos.notifications.domain.model.NotificationTemplate;
import co.empresa.vivaeventos.notifications.domain.repository.INotificationRepository;
import co.empresa.vivaeventos.notifications.domain.repository.INotificationTemplateRepository;
import co.empresa.vivaeventos.notifications.infrastructure.email.IEmailProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    public NotificationServiceImpl(INotificationRepository notificationRepository,
                                  INotificationTemplateRepository templateRepository,
                                  List<IEmailProvider> emailProviders,
                                  TemplateRenderer templateRenderer,
                                  ObjectMapper objectMapper,
                                  co.empresa.vivaeventos.notifications.config.EventsClient eventsClient,
                                  co.empresa.vivaeventos.notifications.config.TicketsClient ticketsClient) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
        this.emailProviders = emailProviders;
        this.templateRenderer = templateRenderer;
        this.objectMapper = objectMapper;
        this.eventsClient = eventsClient;
        this.ticketsClient = ticketsClient;
    }



    private final INotificationRepository notificationRepository;
    private final INotificationTemplateRepository templateRepository;
    private final List<IEmailProvider> emailProviders;
    private final TemplateRenderer templateRenderer;
    private final ObjectMapper objectMapper;
    private final co.empresa.vivaeventos.notifications.config.EventsClient eventsClient;
    private final co.empresa.vivaeventos.notifications.config.TicketsClient ticketsClient;

    @Value("${notifications.retry.initial-delay-seconds:30}")
    private int initialDelaySeconds;

    @Value("${notifications.retry.max-delay-seconds:3600}")
    private int maxDelaySeconds;

    @Override
    @Transactional
    public NotificationResponseDto create(NotificationRequestDto request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTemplateId(request.getTemplateId());
        notification.setEventType(request.getEventType());
        notification.setChannel(request.getChannel());
        notification.setRecipient(request.getRecipient());
        notification.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        notification.setScheduledFor(request.getScheduledFor());
        notification.setStatus("PENDING");
        notification.setRetryCount(0);
        notification.setNextAttempt(LocalDateTime.now());

        if (request.getTemplateId() != null || request.getEventType() != null) {
            resolveTemplate(notification, request);
        } else {
            notification.setSubject(request.getSubject());
            notification.setBody(request.getBody());
        }

        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            try {
                notification.setMetadata(objectMapper.writeValueAsString(request.getVariables()));
            } catch (Exception e) {
                log.warn("Failed to serialize variables metadata", e);
            }
        }

        notification = notificationRepository.save(notification);
        log.info("Notification created: {} for user {} event={} channel={}",
                notification.getId(), notification.getUserId(), notification.getEventType(), notification.getChannel());

        return toResponseDto(notification);
    }

    private void resolveTemplate(Notification notification, NotificationRequestDto request) {
        NotificationTemplate template = null;

        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId()).orElse(null);
        }

        if (template == null && request.getEventType() != null) {
            String templateCode = request.getEventType() + "_" + request.getChannel().toUpperCase();
            template = templateRepository.findByCode(templateCode).orElse(null);
        }

        if (template == null || Boolean.FALSE.equals(template.getIsActive())) {
            notification.setSubject(request.getSubject());
            notification.setBody(request.getBody());
            return;
        }

        String renderedSubject = template.getSubject();
        String renderedBody = template.getBodyTemplate();

        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            renderedSubject = templateRenderer.render(template.getSubject(), request.getVariables());
            renderedBody = templateRenderer.render(template.getBodyTemplate(), request.getVariables());
        }

        notification.setSubject(renderedSubject);
        notification.setBody(renderedBody);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto getById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        return toResponseDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAll() {
        return notificationRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Scheduled(fixedRateString = "${notifications.retry.poll-interval:30000}")
    @Transactional
    public void processPending() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pending = notificationRepository.findPendingToSend(now);

        for (Notification notification : pending) {
            processNotification(notification);
        }

        List<Notification> failed = notificationRepository.findFailedToRetry(now);
        for (Notification notification : failed) {
            processNotification(notification);
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendEventReminders() {
        log.info("Running daily event reminders task...");
        try {
            java.util.List<Map<String, Object>> upcomingEvents = eventsClient.getUpcomingEvents();
            LocalDateTime now = LocalDateTime.now();

            for (Map<String, Object> event : upcomingEvents) {
                UUID eventId = UUID.fromString(String.valueOf(event.get("id")));
                LocalDateTime eventDate = LocalDateTime.parse(String.valueOf(event.get("eventDateTime")));

                if (eventDate.toLocalDate().equals(now.toLocalDate().plusDays(3))) {
                    java.util.List<Map<String, Object>> tickets = ticketsClient.getIssuedTicketsByEvent(eventId);

                    for (Map<String, Object> ticket : tickets) {
                        Object userIdObj = ticket.get("userId");
                        if (userIdObj == null) continue;

                        UUID userId = UUID.fromString(String.valueOf(userIdObj));
                        String holderEmail = (String) ticket.get("holderEmail");
                        String holderName = ticket.get("holderName") instanceof String hn ? hn : (holderEmail != null ? holderEmail : "");
                        String eventName = String.valueOf(event.get("name"));
                        String venueName = event.get("venueName") instanceof String vn ? vn : "";

                        Map<String, String> placeholders = new java.util.HashMap<>();
                        placeholders.put("nombre", holderName);
                        placeholders.put("evento", eventName);
                        placeholders.put("fecha", eventDate.toLocalDate().toString());
                        placeholders.put("lugar", venueName);
                        placeholders.put("hora", eventDate.toLocalTime().toString());
                        placeholders.put("codigo_qr", "Disponible en tu perfil");

                        NotificationRequestDto request = new NotificationRequestDto();
                        request.setUserId(userId);
                        request.setRecipient(holderEmail);
                        request.setChannel("EMAIL");
                        request.setEventType("REMINDER");
                        request.setVariables(placeholders);

                        this.create(request);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during event reminders task: {}", e.getMessage());
        }
    }

    private void processNotification(Notification notification) {
        notification.setStatus("QUEUED");
        notification = notificationRepository.save(notification);

        IEmailProvider provider = resolveProvider(notification.getChannel());

        try {
            provider.send(notification.getRecipient(), notification.getSubject(), notification.getBody());
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notification.setProvider(provider.getProviderName());
            notification.setErrorMessage(null);
            notification.setNextAttempt(null);
            log.info("Notification sent: {} via {} to {}", notification.getId(), provider.getProviderName(), notification.getRecipient());
        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            String msg = e.getMessage();
            notification.setErrorMessage(msg != null && msg.length() > 1000 ? msg.substring(0, 1000) : msg);
            notification.setProvider(provider.getProviderName());

            if (notification.getRetryCount() >= notification.getMaxRetries()) {
                notification.setStatus("FAILED");
                notification.setNextAttempt(null);
                log.error("Notification failed permanently after {} attempts: {} - {}",
                        notification.getRetryCount(), notification.getId(), e.getMessage());
            } else {
                notification.setStatus("QUEUED");
                notification.setNextAttempt(calculateNextAttempt(notification.getRetryCount()));
                log.warn("Notification {} failed (attempt {}/{}), retrying at {}: {}",
                        notification.getId(), notification.getRetryCount(), notification.getMaxRetries(),
                        notification.getNextAttempt(), e.getMessage());
            }
        }

        notificationRepository.save(notification);
    }

    private IEmailProvider resolveProvider(String channel) {
        return emailProviders.stream()
                .filter(p -> p.supports(channel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No email provider supports channel: " + channel));
    }

    private LocalDateTime calculateNextAttempt(int retryCount) {
        long delaySeconds = (long) (initialDelaySeconds * Math.pow(2, retryCount - 1));
        delaySeconds = Math.min(delaySeconds, maxDelaySeconds);
        return LocalDateTime.now().plusSeconds(delaySeconds);
    }

    @Override
    @Transactional
    public void updateDeliveryStatus(UUID notificationId, String status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        notification.setStatus(status);
        if ("DELIVERED".equals(status)) {
            notification.setDeliveredAt(LocalDateTime.now());
        }
        notificationRepository.save(notification);
        log.info("Notification {} status updated to {}", notificationId, status);
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDto createTemplate(NotificationTemplateRequestDto request) {
        NotificationTemplate template = new NotificationTemplate();
        template.setCode(request.getCode());
        template.setName(request.getName());
        template.setChannel(request.getChannel());
        template.setSubject(request.getSubject());
        template.setBodyTemplate(request.getBodyTemplate());
        template.setVariables(request.getVariables());
        template.setIsActive(request.getIsActive());

        template = templateRepository.save(template);
        log.info("Notification template created: {} ({})", template.getCode(), template.getId());

        return toTemplateResponseDto(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDto> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(this::toTemplateResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponseDto getTemplateByCode(String code) {
        NotificationTemplate template = templateRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Template not found: " + code));
        return toTemplateResponseDto(template);
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setTemplateId(notification.getTemplateId());
        dto.setEventType(notification.getEventType());
        dto.setChannel(notification.getChannel());
        dto.setRecipient(notification.getRecipient());
        dto.setSubject(notification.getSubject());
        dto.setBody(notification.getBody());
        dto.setStatus(notification.getStatus());
        dto.setPriority(notification.getPriority());
        dto.setScheduledFor(notification.getScheduledFor());
        dto.setSentAt(notification.getSentAt());
        dto.setDeliveredAt(notification.getDeliveredAt());
        dto.setReadAt(notification.getReadAt());
        dto.setErrorMessage(notification.getErrorMessage());
        dto.setRetryCount(notification.getRetryCount());
        dto.setMaxRetries(notification.getMaxRetries());
        dto.setNextAttempt(notification.getNextAttempt());
        dto.setProvider(notification.getProvider());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private NotificationTemplateResponseDto toTemplateResponseDto(NotificationTemplate template) {
        NotificationTemplateResponseDto dto = new NotificationTemplateResponseDto();
        dto.setId(template.getId());
        dto.setCode(template.getCode());
        dto.setName(template.getName());
        dto.setChannel(template.getChannel());
        dto.setSubject(template.getSubject());
        dto.setBodyTemplate(template.getBodyTemplate());
        dto.setVariables(template.getVariables());
        dto.setIsActive(template.getIsActive());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }
}
