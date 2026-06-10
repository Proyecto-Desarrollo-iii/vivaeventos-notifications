package co.empresa.vivaeventos.notifications.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserPreferenceTest {

    @Test
    @DisplayName("should set createdAt on prePersist")
    void shouldSetCreatedAtOnPrePersist() {
        UserPreference p = new UserPreference();
        assertNull(p.getCreatedAt());
        p.onCreate();
        assertNotNull(p.getCreatedAt());
    }

    @Test
    @DisplayName("should have default notification settings")
    void shouldHaveDefaultNotificationSettings() {
        UserPreference p = new UserPreference();
        assertTrue(p.getEmailNotifications());
        assertTrue(p.getSmsNotifications());
        assertTrue(p.getWhatsappNotifications());
        assertFalse(p.getMarketingOptIn());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetAllFields() {
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
