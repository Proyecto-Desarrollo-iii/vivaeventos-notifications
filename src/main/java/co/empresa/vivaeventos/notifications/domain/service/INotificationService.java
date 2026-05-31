package co.empresa.vivaeventos.notifications.domain.service;

import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationResponseDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateResponseDto;

import java.util.List;
import java.util.UUID;

public interface INotificationService {

    NotificationResponseDto create(NotificationRequestDto request);

    NotificationResponseDto getById(UUID id);

    List<NotificationResponseDto> getAll();

    List<NotificationResponseDto> getByUserId(UUID userId);

    void processPending();

    void updateDeliveryStatus(UUID notificationId, String status);

    NotificationTemplateResponseDto createTemplate(NotificationTemplateRequestDto request);

    List<NotificationTemplateResponseDto> getAllTemplates();

    NotificationTemplateResponseDto getTemplateByCode(String code);
}
