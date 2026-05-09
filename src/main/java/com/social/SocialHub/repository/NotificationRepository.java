package com.social.SocialHub.repository;

import com.social.SocialHub.dto.EntityType;
import com.social.SocialHub.entity.Notification;
import com.social.SocialHub.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.Notation;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(UUID userId);

    boolean findByEntityIdAndEntityType(UUID postId, EntityType entityType);

    int deleteByEntityIdAndEntityType(UUID postId, EntityType entityType);

    int deleteBySenderIdAndReceiverIdAndEntityIdAndEntityTypeAndType(UUID userId, UUID id, UUID postId, EntityType entityType, NotificationType notificationType);
}
