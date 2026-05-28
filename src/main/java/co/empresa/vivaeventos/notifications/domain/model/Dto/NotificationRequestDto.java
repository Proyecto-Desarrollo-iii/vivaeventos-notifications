package co.empresa.vivaeventos.notifications.domain.model.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    @NotNull
    private UUID userId;

    private UUID templateId;

    private String eventType;

    @NotBlank
    private String channel;

    @NotBlank
    private String recipient;

    private String subject;

    private String body;

    private Integer priority;

    private LocalDateTime scheduledFor;

    private Map<String, String> variables;
}
