package co.empresa.vivaeventos.notifications.domain.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private UUID id;
    private UUID userId;
    private UUID templateId;
    private String eventType;
    private String channel;
    private String recipient;
    private String subject;
    private String body;
    private String status;
    private Integer priority;
    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime nextAttempt;
    private String provider;
    private LocalDateTime createdAt;
}
