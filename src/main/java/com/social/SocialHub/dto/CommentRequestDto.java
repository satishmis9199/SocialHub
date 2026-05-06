package com.social.SocialHub.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequestDto {
    private UUID postId;
    private String text;
}