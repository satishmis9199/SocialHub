package com.social.SocialHub.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponseDto {

    private UUID id;
    private UUID postId;
    private UUID userId;

    private String username;
    private String profilePic;

    private String text;
    private LocalDateTime createdAt;
}