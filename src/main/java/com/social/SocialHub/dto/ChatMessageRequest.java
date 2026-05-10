package com.social.SocialHub.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatMessageRequest {

    private UUID receiverId;

    private String message;
}