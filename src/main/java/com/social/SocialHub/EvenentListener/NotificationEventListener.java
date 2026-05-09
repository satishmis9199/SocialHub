package com.social.SocialHub.EvenentListener;

import com.social.SocialHub.dto.PostLikedEvent;
import com.social.SocialHub.entity.Notification;

import com.social.SocialHub.entity.NotificationType;
import com.social.SocialHub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private static final Logger logger= LoggerFactory.getLogger(NotificationEventListener.class);

    @EventListener
    public void handlePostLike(
            PostLikedEvent event) {
        logger.error("Inside Inside handlePostLike");


        if (event.getLikedByUserId()
                .equals(event.getPostOwnerId())) {
            logger.error("User Liked his Own Post");
            return;
        }



        Notification notification =
                Notification.builder()
                        .senderId(event.getLikedByUserId())
                        .receiverId(event.getPostOwnerId())
                        .entityId(event.getPostId())
                        .type(NotificationType.LIKE)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
        logger.error("Notification saved for Like  ");

        notificationRepository.save(notification);
    }
}