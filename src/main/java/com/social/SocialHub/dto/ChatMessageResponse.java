package com.social.SocialHub.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChatMessageResponse {

    private UUID id;

    private UUID senderId;

    private String senderUsername;

    private UUID receiverId;

    private String message;

    private boolean seen;

    private LocalDateTime createdAt;
}