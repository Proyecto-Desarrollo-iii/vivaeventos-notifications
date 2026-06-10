package co.empresa.vivaeventos.notifications.domain.service;

import co.empresa.vivaeventos.notifications.config.EventsClient;
import co.empresa.vivaeventos.notifications.config.TicketsClient;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private INotificationRepository notificationRepository;
    @Mock
    private INotificationTemplateRepository templateRepository;
    @Mock
    private List<IEmailProvider> emailProviders;
    @Mock
    private IEmailProvider emailProvider;
    @Mock
    private TemplateRenderer templateRenderer;
    @Mock
    private EventsClient eventsClient;
    @Mock
    private TicketsClient ticketsClient;

    private ObjectMapper objectMapper;
    private NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new NotificationServiceImpl(
                notificationRepository, templateRepository,
                emailProviders, templateRenderer, objectMapper,
                eventsClient, ticketsClient
        );
        ReflectionTestUtils.setField(service, "initialDelaySeconds", 30);
        ReflectionTestUtils.setField(service, "maxDelaySeconds", 3600);
    }

    @Test
    @DisplayName("create should persist notification and return dto")
    void createShouldPersistNotification() {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId(UUID.randomUUID());
        request.setChannel("EMAIL");
        request.setRecipient("test@test.com");
        request.setSubject("Subject");
        request.setBody("Body");
        request.setPriority(1);

        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());
        saved.setUserId(request.getUserId());
        saved.setChannel("EMAIL");
        saved.setRecipient("test@test.com");
        saved.setSubject("Subject");
        saved.setBody("Body");
        saved.setPriority(1);
        saved.setStatus("PENDING");
        saved.setRetryCount(0);

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDto result = service.create(request);

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("create should resolve template when templateId provided")
    void createShouldResolveTemplateById() {
        UUID templateId = UUID.randomUUID();
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId(UUID.randomUUID());
        request.setTemplateId(templateId);
        request.setChannel("EMAIL");
        request.setRecipient("test@test.com");
        request.setEventType("WELCOME");

        NotificationTemplate template = new NotificationTemplate();
        template.setId(templateId);
        template.setSubject("Bienvenido {{nombre}}");
        template.setBodyTemplate("Hola {{nombre}}");
        template.setIsActive(true);
        template.setCode("WELCOME_EMAIL");

        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());
        saved.setUserId(request.getUserId());
        saved.setChannel("EMAIL");
        saved.setRecipient("test@test.com");
        saved.setSubject(template.getSubject());
        saved.setBody(template.getBodyTemplate());
        saved.setStatus("PENDING");
        saved.setRetryCount(0);

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDto result = service.create(request);

        assertNotNull(result);
        verify(templateRepository).findById(templateId);
    }

    @Test
    @DisplayName("create should resolve template by event type and channel")
    void createShouldResolveTemplateByEventTypeAndChannel() {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId(UUID.randomUUID());
        request.setChannel("EMAIL");
        request.setRecipient("test@test.com");
        request.setEventType("WELCOME");

        NotificationTemplate template = new NotificationTemplate();
        template.setId(UUID.randomUUID());
        template.setSubject("Bienvenido");
        template.setBodyTemplate("Hola");
        template.setIsActive(true);
        template.setCode("WELCOME_EMAIL");

        when(templateRepository.findByCode("WELCOME_EMAIL")).thenReturn(Optional.of(template));

        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());
        saved.setUserId(request.getUserId());
        saved.setChannel("EMAIL");
        saved.setRecipient("test@test.com");
        saved.setSubject(template.getSubject());
        saved.setBody(template.getBodyTemplate());
        saved.setStatus("PENDING");
        saved.setRetryCount(0);

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDto result = service.create(request);

        assertNotNull(result);
        verify(templateRepository).findByCode("WELCOME_EMAIL");
    }

    @Test
    @DisplayName("create should use fallback subject/body when no template found")
    void createShouldUseFallbackWhenNoTemplate() {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId(UUID.randomUUID());
        request.setChannel("EMAIL");
        request.setRecipient("test@test.com");
        request.setEventType("UNKNOWN");
        request.setSubject("Fallback Subject");
        request.setBody("Fallback Body");

        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());
        saved.setUserId(request.getUserId());
        saved.setChannel("EMAIL");
        saved.setRecipient("test@test.com");
        saved.setSubject("Fallback Subject");
        saved.setBody("Fallback Body");
        saved.setStatus("PENDING");
        saved.setRetryCount(0);

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDto result = service.create(request);

        assertNotNull(result);
        assertEquals("Fallback Subject", result.getSubject());
        assertEquals("Fallback Body", result.getBody());
    }

    @Test
    @DisplayName("getById should return notification when found")
    void getByIdShouldReturnNotification() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setUserId(UUID.randomUUID());
        notification.setChannel("EMAIL");
        notification.setRecipient("test@test.com");
        notification.setStatus("SENT");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        NotificationResponseDto result = service.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("getById should throw when not found")
    void getByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getById(id));
        assertTrue(ex.getMessage().contains("Notification not found"));
    }

    @Test
    @DisplayName("getAll should return all notifications")
    void getAllShouldReturnAll() {
        Notification n1 = new Notification();
        n1.setId(UUID.randomUUID());
        n1.setUserId(UUID.randomUUID());
        n1.setChannel("EMAIL");
        n1.setRecipient("a@a.com");
        n1.setStatus("SENT");

        Notification n2 = new Notification();
        n2.setId(UUID.randomUUID());
        n2.setUserId(UUID.randomUUID());
        n2.setChannel("EMAIL");
        n2.setRecipient("b@b.com");
        n2.setStatus("PENDING");

        when(notificationRepository.findAll()).thenReturn(List.of(n1, n2));

        List<NotificationResponseDto> result = service.getAll();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getByUserId should return user notifications")
    void getByUserIdShouldReturnUserNotifications() {
        UUID userId = UUID.randomUUID();
        Notification n = new Notification();
        n.setId(UUID.randomUUID());
        n.setUserId(userId);
        n.setChannel("EMAIL");
        n.setRecipient("test@test.com");
        n.setStatus("SENT");

        when(notificationRepository.findByUserId(userId)).thenReturn(List.of(n));

        List<NotificationResponseDto> result = service.getByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.getFirst().getUserId());
    }

    @Test
    @DisplayName("updateDeliveryStatus should update status and deliveredAt")
    void updateDeliveryStatusShouldUpdate() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus("SENT");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        service.updateDeliveryStatus(id, "DELIVERED");

        assertEquals("DELIVERED", notification.getStatus());
        assertNotNull(notification.getDeliveredAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("updateDeliveryStatus should not set deliveredAt for non-DELIVERED")
    void updateDeliveryStatusShouldNotSetDeliveredAtForNonDelivered() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus("PENDING");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        service.updateDeliveryStatus(id, "READ");

        assertEquals("READ", notification.getStatus());
        assertNull(notification.getDeliveredAt());
    }

    @Test
    @DisplayName("createTemplate should persist and return dto")
    void createTemplateShouldPersist() {
        NotificationTemplateRequestDto request = new NotificationTemplateRequestDto();
        request.setCode("WELCOME_EMAIL");
        request.setName("Welcome Email");
        request.setChannel("EMAIL");
        request.setSubject("Welcome");
        request.setBodyTemplate("Hello {{nombre}}");
        request.setIsActive(true);

        NotificationTemplate saved = new NotificationTemplate();
        saved.setId(UUID.randomUUID());
        saved.setCode("WELCOME_EMAIL");
        saved.setName("Welcome Email");
        saved.setChannel("EMAIL");
        saved.setSubject("Welcome");
        saved.setBodyTemplate("Hello {{nombre}}");
        saved.setIsActive(true);

        when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(saved);

        NotificationTemplateResponseDto result = service.createTemplate(request);

        assertNotNull(result);
        assertEquals("WELCOME_EMAIL", result.getCode());
        verify(templateRepository).save(any(NotificationTemplate.class));
    }

    @Test
    @DisplayName("getAllTemplates should return all templates")
    void getAllTemplatesShouldReturnAll() {
        NotificationTemplate t = new NotificationTemplate();
        t.setId(UUID.randomUUID());
        t.setCode("WELCOME_EMAIL");
        t.setName("Welcome");
        t.setChannel("EMAIL");
        t.setSubject("Welcome");
        t.setBodyTemplate("Hello");
        t.setIsActive(true);

        when(templateRepository.findAll()).thenReturn(List.of(t));

        List<NotificationTemplateResponseDto> result = service.getAllTemplates();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTemplateByCode should return template when found")
    void getTemplateByCodeShouldReturn() {
        String code = "WELCOME_EMAIL";
        NotificationTemplate t = new NotificationTemplate();
        t.setId(UUID.randomUUID());
        t.setCode(code);
        t.setName("Welcome");
        t.setChannel("EMAIL");
        t.setSubject("Welcome");
        t.setBodyTemplate("Hello");
        t.setIsActive(true);

        when(templateRepository.findByCode(code)).thenReturn(Optional.of(t));

        NotificationTemplateResponseDto result = service.getTemplateByCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
    }

    @Test
    @DisplayName("getTemplateByCode should throw when not found")
    void getTemplateByCodeShouldThrowWhenNotFound() {
        String code = "NONEXISTENT";
        when(templateRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getTemplateByCode(code));
    }

    @Test
    @DisplayName("processPending should process pending and failed notifications")
    void processPendingShouldProcessBothQueues() {
        Notification pending = new Notification();
        pending.setId(UUID.randomUUID());
        pending.setChannel("EMAIL");
        pending.setRecipient("test@test.com");
        pending.setSubject("Test");
        pending.setBody("Body");
        pending.setStatus("PENDING");
        pending.setRetryCount(0);
        pending.setMaxRetries(5);

        when(emailProviders.stream()).thenReturn(List.of(emailProvider).stream());
        when(emailProvider.supports("EMAIL")).thenReturn(true);
        when(emailProvider.getProviderName()).thenReturn("test-provider");

        when(notificationRepository.findPendingToSend(any(LocalDateTime.class))).thenReturn(List.of(pending));
        when(notificationRepository.findFailedToRetry(any(LocalDateTime.class))).thenReturn(List.of());

        doNothing().when(emailProvider).send(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(pending);

        service.processPending();

        verify(emailProvider).send("test@test.com", "Test", "Body");
        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    @DisplayName("processPending should handle send failure and retry")
    void processPendingShouldHandleFailure() {
        Notification pending = new Notification();
        pending.setId(UUID.randomUUID());
        pending.setChannel("EMAIL");
        pending.setRecipient("test@test.com");
        pending.setSubject("Test");
        pending.setBody("Body");
        pending.setStatus("PENDING");
        pending.setRetryCount(0);
        pending.setMaxRetries(5);

        when(emailProviders.stream()).thenReturn(List.of(emailProvider).stream());
        when(emailProvider.supports("EMAIL")).thenReturn(true);

        when(notificationRepository.findPendingToSend(any(LocalDateTime.class))).thenReturn(List.of(pending));
        when(notificationRepository.findFailedToRetry(any(LocalDateTime.class))).thenReturn(List.of());

        doThrow(new RuntimeException("SMTP error")).when(emailProvider).send(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(pending);

        service.processPending();

        assertEquals(1, pending.getRetryCount());
        assertNotNull(pending.getNextAttempt());
    }

    @Test
    @DisplayName("processPending should mark as failed after max retries")
    void processPendingShouldMarkFailedAfterMaxRetries() {
        Notification pending = new Notification();
        pending.setId(UUID.randomUUID());
        pending.setChannel("EMAIL");
        pending.setRecipient("test@test.com");
        pending.setSubject("Test");
        pending.setBody("Body");
        pending.setStatus("QUEUED");
        pending.setRetryCount(5);
        pending.setMaxRetries(5);

        when(emailProviders.stream()).thenReturn(List.of(emailProvider).stream());
        when(emailProvider.supports("EMAIL")).thenReturn(true);

        when(notificationRepository.findPendingToSend(any(LocalDateTime.class))).thenReturn(List.of());
        when(notificationRepository.findFailedToRetry(any(LocalDateTime.class))).thenReturn(List.of(pending));

        doThrow(new RuntimeException("Final error")).when(emailProvider).send(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(pending);

        service.processPending();

        assertEquals("FAILED", pending.getStatus());
        assertNull(pending.getNextAttempt());
    }
}
