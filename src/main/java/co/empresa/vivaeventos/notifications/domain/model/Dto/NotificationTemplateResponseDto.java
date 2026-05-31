package co.empresa.vivaeventos.notifications.domain.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateResponseDto {

    private UUID id;
    private String code;
    private String name;
    private String channel;
    private String subject;
    private String bodyTemplate;
    private String[] variables;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
