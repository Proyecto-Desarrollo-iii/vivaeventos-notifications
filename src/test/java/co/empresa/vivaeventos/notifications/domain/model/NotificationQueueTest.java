package co.empresa.vivaeventos.notifications.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationQueueTest {

    @Test
    @DisplayName("should set createdAt on prePersist")
    void shouldSetCreatedAtOnPrePersist() {
        NotificationQueue q = new NotificationQueue();
        assertNull(q.getCreatedAt());
        q.onCreate();
        assertNotNull(q.getCreatedAt());
    }

    @Test
    @DisplayName("should have default status PENDING")
    void shouldHaveDefaultStatusPending() {
        NotificationQueue q = new NotificationQueue();
        assertEquals("PENDING", q.getStatus());
    }

    @Test
    @DisplayName("should have default attempts 0")
    void shouldHaveDefaultAttempts() {
        NotificationQueue q = new NotificationQueue();
        assertEquals(0, q.getAttempts());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetAllFields() {
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
}
