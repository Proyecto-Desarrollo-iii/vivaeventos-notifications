package co.empresa.vivaeventos.notifications.delivery.rest;

import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationResponseDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateRequestDto;
import co.empresa.vivaeventos.notifications.domain.model.Dto.NotificationTemplateResponseDto;
import co.empresa.vivaeventos.notifications.domain.service.INotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@Valid @RequestBody NotificationRequestDto request) {
        NotificationResponseDto response = notificationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getById(@PathVariable UUID id) {
        NotificationResponseDto response = notificationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAll() {
        List<NotificationResponseDto> response = notificationService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getByUserId(@PathVariable UUID userId) {
        List<NotificationResponseDto> response = notificationService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process")
    public ResponseEntity<Void> processPending() {
        notificationService.processPending();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationService.updateDeliveryStatus(id, "READ");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/templates")
    public ResponseEntity<NotificationTemplateResponseDto> createTemplate(@Valid @RequestBody NotificationTemplateRequestDto request) {
        NotificationTemplateResponseDto response = notificationService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<NotificationTemplateResponseDto>> getAllTemplates() {
        List<NotificationTemplateResponseDto> response = notificationService.getAllTemplates();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/templates/{code}")
    public ResponseEntity<NotificationTemplateResponseDto> getTemplateByCode(@PathVariable String code) {
        NotificationTemplateResponseDto response = notificationService.getTemplateByCode(code);
        return ResponseEntity.ok(response);
    }
}
