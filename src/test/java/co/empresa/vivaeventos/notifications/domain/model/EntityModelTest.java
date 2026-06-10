package co.empresa.vivaeventos.notifications.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityModelTest {

    @Test
    @DisplayName("Notification should set createdAt on prePersist")
    void notificationShouldSetCreatedAtOnPrePersist() {
        Notification notification = new Notification();
        assertNull(notification.getCreatedAt());
        notification.onCreate();
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    @DisplayName("Notification should have default status PENDING")
    void notificationShouldHaveDefaultStatusPending() {
        Notification notification = new Notification();
        assertEquals("PENDING", notification.getStatus());
    }

    @Test
    @DisplayName("Notification should have default retryCount 0")
    void notificationShouldHaveDefaultRetryCount() {
        Notification notification = new Notification();
        assertEquals(0, notification.getRetryCount());
    }

    @Test
    @DisplayName("Notification should have default maxRetries 5")
    void notificationShouldHaveDefaultMaxRetries() {
        Notification notification = new Notification();
        assertEquals(5, notification.getMaxRetries());
    }

    @Test
    @DisplayName("Notification should have default priority 0")
    void notificationShouldHaveDefaultPriority() {
        Notification notification = new Notification();
        assertEquals(0, notification.getPriority());
    }

    @Test
    @DisplayName("Notification should set and get all fields")
    void notificationShouldSetAndGetAllFields() {
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

    @Test
    @DisplayName("NotificationTemplate should set timestamps on prePersist")
    void templateShouldSetTimestampsOnPrePersist() {
        NotificationTemplate t = new NotificationTemplate();
        assertNull(t.getCreatedAt());
        assertNull(t.getUpdatedAt());
        t.onCreate();
        assertNotNull(t.getCreatedAt());
        assertNotNull(t.getUpdatedAt());
    }

    @Test
    @DisplayName("NotificationTemplate should update updatedAt on preUpdate")
    void templateShouldUpdateUpdatedAtOnPreUpdate() {
        NotificationTemplate t = new NotificationTemplate();
        t.onUpdate();
        assertNotNull(t.getUpdatedAt());
    }

    @Test
    @DisplayName("NotificationTemplate should have default isActive true")
    void templateShouldHaveDefaultIsActive() {
        NotificationTemplate t = new NotificationTemplate();
        assertTrue(t.getIsActive());
    }

    @Test
    @DisplayName("NotificationTemplate should set and get all fields")
    void templateShouldSetAndGetAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        NotificationTemplate t = new NotificationTemplate();
        t.setId(id);
        t.setCode("WELCOME_EMAIL");
        t.setName("Welcome Email");
        t.setChannel("EMAIL");
        t.setSubject("Welcome");
        t.setBodyTemplate("Hello {{nombre}}");
        t.setVariables(new String[]{"nombre", "evento"});
        t.setIsActive(false);

        assertEquals(id, t.getId());
        assertEquals("WELCOME_EMAIL", t.getCode());
        assertEquals("Welcome Email", t.getName());
        assertEquals("EMAIL", t.getChannel());
        assertEquals("Welcome", t.getSubject());
        assertEquals("Hello {{nombre}}", t.getBodyTemplate());
        assertArrayEquals(new String[]{"nombre", "evento"}, t.getVariables());
        assertFalse(t.getIsActive());
    }

    @Test
    @DisplayName("NotificationQueue should set createdAt on prePersist")
    void queueShouldSetCreatedAtOnPrePersist() {
        NotificationQueue q = new NotificationQueue();
        assertNull(q.getCreatedAt());
        q.onCreate();
        assertNotNull(q.getCreatedAt());
    }

    @Test
    @DisplayName("NotificationQueue should have default status PENDING")
    void queueShouldHaveDefaultStatusPending() {
        NotificationQueue q = new NotificationQueue();
        assertEquals("PENDING", q.getStatus());
    }

    @Test
    @DisplayName("NotificationQueue should have default attempts 0")
    void queueShouldHaveDefaultAttempts() {
        NotificationQueue q = new NotificationQueue();
        assertEquals(0, q.getAttempts());
    }

    @Test
    @DisplayName("NotificationQueue should set and get all fields")
    void queueShouldSetAndGetAllFields() {
        UUID id = UUID.randomUUID();
        UUID notifId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        NotificationQueue q = new NotificationQueue();
        q.setId(id);
        q.setNotificationId(notifId);
        q.setProvider("sendgrid");
        q.setStatus("SENT");
        q.setAttempts(2);
        q.setLastAttempt(now);
        q.setNextAttempt(now);
        q.setErrorMessage("timeout");

        assertEquals(id, q.getId());
        assertEquals(notifId, q.getNotificationId());
        assertEquals("sendgrid", q.getProvider());
        assertEquals("SENT", q.getStatus());
        assertEquals(2, q.getAttempts());
        assertEquals(now, q.getLastAttempt());
        assertEquals(now, q.getNextAttempt());
        assertEquals("timeout", q.getErrorMessage());
    }

    @Test
    @DisplayName("UserPreference should set createdAt on prePersist")
    void preferenceShouldSetCreatedAtOnPrePersist() {
        UserPreference p = new UserPreference();
        assertNull(p.getCreatedAt());
        p.onCreate();
        assertNotNull(p.getCreatedAt());
    }

    @Test
    @DisplayName("UserPreference should have default notification settings")
    void preferenceShouldHaveDefaultNotificationSettings() {
        UserPreference p = new UserPreference();
        assertTrue(p.getEmailNotifications());
        assertTrue(p.getSmsNotifications());
        assertTrue(p.getWhatsappNotifications());
        assertFalse(p.getMarketingOptIn());
    }

    @Test
    @DisplayName("UserPreference should set and get all fields")
    void preferenceShouldSetAndGetAllFields() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserPreference p = new UserPreference();
        p.setId(id);
        p.setUserId(userId);
        p.setEmailNotifications(false);
        p.setSmsNotifications(false);
        p.setWhatsappNotifications(false);
        p.setMarketingOptIn(true);

        assertEquals(id, p.getId());
        assertEquals(userId, p.getUserId());
        assertFalse(p.getEmailNotifications());
        assertFalse(p.getSmsNotifications());
        assertFalse(p.getWhatsappNotifications());
        assertTrue(p.getMarketingOptIn());
    }
}
