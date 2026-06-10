package co.empresa.vivaeventos.notifications.delivery.rest;

import co.empresa.vivaeventos.notifications.domain.service.INotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private INotificationService notificationService;

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/sendgrid should process events")
    void handleSendGridEventShouldProcess() throws Exception {
        UUID notifId = UUID.randomUUID();
        List<Map<String, Object>> events = List.of(
                Map.of("event", "delivered", "sg_message_id", notifId + ".filter123"),
                Map.of("event", "open", "sg_message_id", UUID.randomUUID() + ".filter456")
        );

        mockMvc.perform(post("/api/v1/webhooks/notifications/sendgrid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(events)))
                .andExpect(status().isOk());

        verify(notificationService).updateDeliveryStatus(any(UUID.class), eq("DELIVERED"));
        verify(notificationService).updateDeliveryStatus(any(UUID.class), eq("READ"));
    }

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/sendgrid should skip events with null sg_message_id")
    void handleSendGridEventShouldSkipNullMessageId() throws Exception {
        List<Map<String, Object>> events = List.of(
                Map.of("event", "delivered")
        );

        mockMvc.perform(post("/api/v1/webhooks/notifications/sendgrid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(events)))
                .andExpect(status().isOk());

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/sendgrid should map bounce to FAILED")
    void handleSendGridEventShouldMapBounceToFailed() throws Exception {
        List<Map<String, Object>> events = List.of(
                Map.of("event", "bounce", "sg_message_id", UUID.randomUUID() + ".f1"),
                Map.of("event", "dropped", "sg_message_id", UUID.randomUUID() + ".f2"),
                Map.of("event", "spamreport", "sg_message_id", UUID.randomUUID() + ".f3")
        );

        mockMvc.perform(post("/api/v1/webhooks/notifications/sendgrid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(events)))
                .andExpect(status().isOk());

        verify(notificationService, times(3)).updateDeliveryStatus(any(UUID.class), eq("FAILED"));
    }

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/generic should process event")
    void handleGenericEventShouldProcess() throws Exception {
        UUID notifId = UUID.randomUUID();
        Map<String, Object> payload = Map.of("notificationId", notifId.toString(), "status", "DELIVERED");

        mockMvc.perform(post("/api/v1/webhooks/notifications/generic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(notificationService).updateDeliveryStatus(notifId, "DELIVERED");
    }

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/generic should skip when missing fields")
    void handleGenericEventShouldSkipMissingFields() throws Exception {
        Map<String, Object> payload = Map.of("notificationId", "some-id");

        mockMvc.perform(post("/api/v1/webhooks/notifications/generic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("POST /api/v1/webhooks/notifications/sendgrid should handle malformed events gracefully")
    void handleSendGridEventShouldHandleMalformedEvents() throws Exception {
        List<Map<String, Object>> events = List.of(
                Map.of("event", "delivered", "sg_message_id", "not-a-uuid.with-dot")
        );

        mockMvc.perform(post("/api/v1/webhooks/notifications/sendgrid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(events)))
                .andExpect(status().isOk());
    }
}
