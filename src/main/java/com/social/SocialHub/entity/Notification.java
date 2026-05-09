package com.social.SocialHub.entity;


import com.social.SocialHub.dto.EntityType;
import com.social.SocialHub.entity.NotificationPriority;
import com.social.SocialHub.entity.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        indexes = {

                // Fast user notification fetch
                @Index(
                        name = "idx_receiver_created",
                        columnList = "receiver_id, created_at"
                ),

                // Fast unread count
                @Index(
                        name = "idx_receiver_read",
                        columnList = "receiver_id, is_read"
                ),

                // Fast sender lookup
                @Index(
                        name = "idx_sender",
                        columnList = "sender_id"
                ),

                // Fast type filtering
                @Index(
                        name = "idx_type",
                        columnList = "type"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;


    @Column(name = "entity_id")
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;

    /**
     * Read/unread state
     */
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private NotificationPriority priority;


    @Column(columnDefinition = "TEXT")
    private String metadata;
    @Column(name = "is_delivered")
    private boolean delivered;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "read_at")
    private LocalDateTime readAt;
}