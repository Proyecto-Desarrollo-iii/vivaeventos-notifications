package co.empresa.vivaeventos.notifications.domain.repository;

import co.empresa.vivaeventos.notifications.domain.model.NotificationQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface INotificationQueueRepository extends JpaRepository<NotificationQueue, UUID> {

    List<NotificationQueue> findByNotificationId(UUID notificationId);

    List<NotificationQueue> findByStatus(String status);
}
