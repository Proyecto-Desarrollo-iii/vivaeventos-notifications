package co.empresa.vivaeventos.notifications.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTemplateTest {

    @Test
    @DisplayName("should set timestamps on prePersist")
    void shouldSetTimestampsOnPrePersist() {
        NotificationTemplate t = new NotificationTemplate();
        assertNull(t.getCreatedAt());
        assertNull(t.getUpdatedAt());
        t.onCreate();
        assertNotNull(t.getCreatedAt());
        assertNotNull(t.getUpdatedAt());
    }

    @Test
    @DisplayName("should update updatedAt on preUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() {
        NotificationTemplate t = new NotificationTemplate();
        t.onUpdate();
        assertNotNull(t.getUpdatedAt());
    }

    @Test
    @DisplayName("should have default isActive true")
    void shouldHaveDefaultIsActive() {
        NotificationTemplate t = new NotificationTemplate();
        assertEquals(true, t.getIsActive());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetAllFields() {
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
}
