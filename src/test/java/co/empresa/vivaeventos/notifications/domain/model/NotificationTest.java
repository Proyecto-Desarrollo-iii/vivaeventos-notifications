package co.empresa.vivaeventos.notifications.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    @DisplayName("should set createdAt on prePersist")
    void shouldSetCreatedAtOnPrePersist() {
        Notification notification = new Notification();
        assertNull(notification.getCreatedAt());
        notification.onCreate();
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    @DisplayName("should have default status PENDING")
    void shouldHaveDefaultStatusPending() {
        Notification notification = new Notification();
        assertEquals("PENDING", notification.getStatus());
    }

    @Test
    @DisplayName("should have default retryCount 0")
    void shouldHaveDefaultRetryCount() {
        Notification notification = new Notification();
        assertEquals(0, notification.getRetryCount());
    }

    @Test
    @DisplayName("should have default maxRetries 5")
    void shouldHaveDefaultMaxRetries() {
        Notification notification = new Notification();
        assertEquals(5, notification.getMaxRetries());
    }

    @Test
    @DisplayName("should have default priority 0")
    void shouldHaveDefaultPriority() {
        Notification notification = new Notification();
        assertEquals(0, notification.getPriority());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetAllFields() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Notification n = new Notification();
        n.setId(id);
        n.setUserId(userId);
        n.setTemplateId(templateId);
        n.setEventType("WELCOME");
        n.setChannel("EMAIL");
        n.setRecipient("test@test.com");
        n.setSubject("Welcome");
        n.setBody("Hello");
        n.setStatus("SENT");
        n.setPriority(2);
        n.setScheduledFor(now);
        n.setSentAt(now);
        n.setDeliveredAt(now);
        n.setReadAt(now);
        n.setErrorMessage("none");
        n.setRetryCount(1);
        n.setMaxRetries(3);
        n.setNextAttempt(now);
        n.setProvider("sendgrid");
        n.setMetadata("{\"key\":\"value\"}");

        assertEquals(id, n.getId());
        assertEquals(userId, n.getUserId());
        assertEquals(templateId, n.getTemplateId());
        assertEquals("WELCOME", n.getEventType());
        assertEquals("EMAIL", n.getChannel());
        assertEquals("test@test.com", n.getRecipient());
        assertEquals("Welcome", n.getSubject());
        assertEquals("Hello", n.getBody());
        assertEquals("SENT", n.getStatus());
        assertEquals(2, n.getPriority());
        assertEquals(now, n.getScheduledFor());
        assertEquals(now, n.getSentAt());
        assertEquals(now, n.getDeliveredAt());
        assertEquals(now, n.getReadAt());
        assertEquals("none", n.getErrorMessage());
        assertEquals(1, n.getRetryCount());
        assertEquals(3, n.getMaxRetries());
        assertEquals(now, n.getNextAttempt());
        assertEquals("sendgrid", n.getProvider());
        assertEquals("{\"key\":\"value\"}", n.getMetadata());
    }
}
