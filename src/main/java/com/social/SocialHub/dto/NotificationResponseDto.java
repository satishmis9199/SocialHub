package com.social.SocialHub.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {

    private UUID id;

    private String senderUsername;

    private String senderProfile;

    private String message;

    private boolean isRead;

    private LocalDateTime createdAt;
}