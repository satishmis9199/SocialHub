package com.social.SocialHub.service;

import com.social.SocialHub.dto.NotificationResponseDto;
import com.social.SocialHub.entity.Notification;
import com.social.SocialHub.entity.NotificationType;
import com.social.SocialHub.entity.UserEntity;

import com.social.SocialHub.repository.NotificationRepository;
import com.social.SocialHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    public List<NotificationResponseDto> getNotifications(
            UUID userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByReceiverIdOrderByCreatedAtDesc(
                                userId
                        );

        List<NotificationResponseDto> response =
                new ArrayList<>();

        for (Notification notification : notifications) {

            NotificationResponseDto dto =
                    mapToDto(notification);

            response.add(dto);
        }

        return response;
    }

    private NotificationResponseDto mapToDto(
            Notification notification) {

        UserEntity sender =
                userRepository.findById(
                        notification.getSenderId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Sender not found"
                        )
                );

        String message = "";

        if (notification.getType()
                == NotificationType.LIKE) {

            message =
                    sender.getUsername()
                            + " liked your post";
        }

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .senderUsername(sender.getUsername())
                .senderProfile(sender.getProfilePic())
                .message(message)
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    @Transactional
    public void markAsRead(UUID id) {
        logger.error("Notifiacation marked a s Read");


        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}