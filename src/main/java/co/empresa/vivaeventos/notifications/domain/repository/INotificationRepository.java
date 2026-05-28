package co.empresa.vivaeventos.notifications.domain.repository;

import co.empresa.vivaeventos.notifications.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserId(UUID userId);

    List<Notification> findByStatus(String status);

    List<Notification> findByEventType(String eventType);

    @Query("SELECT n FROM Notification n WHERE n.status IN ('PENDING', 'QUEUED') AND n.retryCount < n.maxRetries AND (n.scheduledFor IS NULL OR n.scheduledFor <= :now) AND (n.nextAttempt IS NULL OR n.nextAttempt <= :now)")
    List<Notification> findPendingToSend(@Param("now") LocalDateTime now);

    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries AND n.nextAttempt <= :now")
    List<Notification> findFailedToRetry(@Param("now") LocalDateTime now);
}
