package co.empresa.vivaeventos.notifications.delivery.rest;

import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationResponseDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateResponseDto;
import co.empresa.vivaeventos.notifications.domain.service.INotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private INotificationService notificationService;

    @Test
    @DisplayName("POST /api/v1/notifications should return 201")
    void createShouldReturn201() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setUserId(UUID.randomUUID());
        request.setChannel("EMAIL");
        request.setRecipient("test@test.com");
        request.setSubject("Subject");
        request.setBody("Body");

        NotificationResponseDto response = new NotificationResponseDto();
        response.setId(UUID.randomUUID());
        response.setUserId(request.getUserId());
        response.setChannel("EMAIL");
        response.setRecipient("test@test.com");
        response.setSubject("Subject");
        response.setBody("Body");
        response.setStatus("PENDING");

        when(notificationService.create(any(NotificationRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/{id} should return 200")
    void getByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        NotificationResponseDto response = new NotificationResponseDto();
        response.setId(id);
        response.setUserId(UUID.randomUUID());
        response.setChannel("EMAIL");
        response.setRecipient("test@test.com");
        response.setStatus("SENT");

        when(notificationService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return 200 with list")
    void getAllShouldReturn200() throws Exception {
        NotificationResponseDto n1 = new NotificationResponseDto();
        n1.setId(UUID.randomUUID());
        n1.setUserId(UUID.randomUUID());
        n1.setChannel("EMAIL");
        n1.setRecipient("a@a.com");
        n1.setStatus("SENT");

        when(notificationService.getAll()).thenReturn(List.of(n1));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/user/{userId} should return 200")
    void getByUserIdShouldReturn200() throws Exception {
        UUID userId = UUID.randomUUID();
        NotificationResponseDto n = new NotificationResponseDto();
        n.setId(UUID.randomUUID());
        n.setUserId(userId);
        n.setChannel("EMAIL");
        n.setRecipient("test@test.com");
        n.setStatus("SENT");

        when(notificationService.getByUserId(userId)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/v1/notifications/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/notifications/process should return 200")
    void processPendingShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/notifications/process"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/{id}/read should return 200")
    void markAsReadShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(patch("/api/v1/notifications/{id}/read", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/notifications/templates should return 201")
    void createTemplateShouldReturn201() throws Exception {
        NotificationTemplateRequestDto request = new NotificationTemplateRequestDto();
        request.setCode("WELCOME_EMAIL");
        request.setName("Welcome");
        request.setChannel("EMAIL");
        request.setSubject("Welcome");
        request.setBodyTemplate("Hello {{nombre}}");
        request.setIsActive(true);

        NotificationTemplateResponseDto response = new NotificationTemplateResponseDto();
        response.setId(UUID.randomUUID());
        response.setCode("WELCOME_EMAIL");
        response.setName("Welcome");
        response.setChannel("EMAIL");
        response.setSubject("Welcome");
        response.setBodyTemplate("Hello {{nombre}}");
        response.setIsActive(true);

        when(notificationService.createTemplate(any(NotificationTemplateRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notifications/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("WELCOME_EMAIL"));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/templates should return 200")
    void getAllTemplatesShouldReturn200() throws Exception {
        NotificationTemplateResponseDto t = new NotificationTemplateResponseDto();
        t.setId(UUID.randomUUID());
        t.setCode("WELCOME_EMAIL");
        t.setName("Welcome");
        t.setChannel("EMAIL");
        t.setIsActive(true);

        when(notificationService.getAllTemplates()).thenReturn(List.of(t));

        mockMvc.perform(get("/api/v1/notifications/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/templates/{code} should return 200")
    void getTemplateByCodeShouldReturn200() throws Exception {
        String code = "WELCOME_EMAIL";
        NotificationTemplateResponseDto t = new NotificationTemplateResponseDto();
        t.setId(UUID.randomUUID());
        t.setCode(code);
        t.setName("Welcome");
        t.setChannel("EMAIL");
        t.setIsActive(true);

        when(notificationService.getTemplateByCode(code)).thenReturn(t);

        mockMvc.perform(get("/api/v1/notifications/templates/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code));
    }
}
