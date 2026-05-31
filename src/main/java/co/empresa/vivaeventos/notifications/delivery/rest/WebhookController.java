package co.empresa.vivaeventos.notifications.delivery.rest;

import co.empresa.vivaeventos.notifications.domain.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks/notifications")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final INotificationService notificationService;

    @PostMapping("/sendgrid")
    public ResponseEntity<Void> handleSendGridEvent(@RequestBody List<Map<String, Object>> events) {
        for (Map<String, Object> event : events) {
            try {
                String eventType = (String) event.get("event");
                Object sgMessageId = event.get("sg_message_id");
                if (sgMessageId == null) continue;

                String notificationId = extractNotificationId(sgMessageId.toString());
                if (notificationId == null) continue;

                String status = mapSendGridEvent(eventType);
                if (status != null) {
                    notificationService.updateDeliveryStatus(UUID.fromString(notificationId), status);
                    log.info("Webhook: notification {} -> {}", notificationId, status);
                }
            } catch (Exception e) {
                log.warn("Failed to process webhook event: {}", event, e);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generic")
    public ResponseEntity<Void> handleGenericEvent(@RequestBody Map<String, Object> payload) {
        String notificationId = (String) payload.get("notificationId");
        String status = (String) payload.get("status");

        if (notificationId != null && status != null) {
            notificationService.updateDeliveryStatus(UUID.fromString(notificationId), status);
            log.info("Generic webhook: notification {} -> {}", notificationId, status);
        }

        return ResponseEntity.ok().build();
    }

    private String extractNotificationId(String sgMessageId) {
        if (sgMessageId == null || sgMessageId.isBlank()) return null;
        int dotIndex = sgMessageId.indexOf('.');
        return dotIndex > 0 ? sgMessageId.substring(0, dotIndex) : sgMessageId;
    }

    private String mapSendGridEvent(String sendGridEvent) {
        if (sendGridEvent == null) return null;
        return switch (sendGridEvent) {
            case "delivered" -> "DELIVERED";
            case "open" -> "READ";
            case "bounce", "dropped", "spamreport" -> "FAILED";
            default -> null;
        };
    }
}
