package com.social.SocialHub.EvenentListener;

import com.social.SocialHub.controller.OnlineUsers;
import com.social.SocialHub.dto.EntityType;
import com.social.SocialHub.dto.PostLikedEvent;
import com.social.SocialHub.entity.Notification;
import com.social.SocialHub.entity.NotificationType;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.NotificationRepository;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final FirebaseService firebaseService;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handlePostLike(
            PostLikedEvent event
    ) {

        log.error(
                "POST LIKE EVENT RECEIVED"
        );

        if (
                event.getLikedByUserId()
                        .equals(
                                event.getPostOwnerId()
                        )
        ) {

            log.error(
                    "USER LIKED OWN POST"
            );

            return;
        }

        UserEntity sender =
                userRepository.findById(
                        event.getLikedByUserId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Sender not found"
                        )
                );

        UserEntity receiver =
                userRepository.findById(
                        event.getPostOwnerId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Receiver not found"
                        )
                );

        Notification notification =
                Notification.builder()

                        .senderId(
                                sender.getId()
                        )

                        .receiverId(
                                receiver.getId()
                        )

                        .entityId(
                                event.getPostId()
                        )

                        .entityType(
                                EntityType.POST
                        )

                        .type(
                                NotificationType.LIKE
                        )

                        .isRead(false)

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();

        notificationRepository.save(
                notification
        );

        log.error(
                "NOTIFICATION SAVED"
        );

        messagingTemplate.convertAndSendToUser(

                receiver.getUsername(),

                "/queue/notifications",

                Map.of(

                        "type",
                        "LIKE",

                        "senderUsername",
                        sender.getUsername(),

                        "message",
                        sender.getUsername()
                                + " liked your post"
                )
        );

        log.error(
                "REALTIME NOTIFICATION SENT TO = {}",
                receiver.getEmail()
        );



        if(

                !OnlineUsers
                        .ONLINE_USERS
                        .contains(
                                receiver.getUsername()
                        )

                        &&

                        receiver.getFcmToken() != null
        ){

            firebaseService.sendPushNotification(

                    receiver.getFcmToken(),

                    sender.getUsername(),
                   sender.getUsername()
                            + " liked your post"
            );

            log.error(
                    "FIREBASE PUSH SENT TO = {}",
                    receiver.getUsername()
            );
        }
    }
}