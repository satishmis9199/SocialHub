package com.social.SocialHub.repository;

import com.social.SocialHub.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.Notation;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(UUID userId);
}
