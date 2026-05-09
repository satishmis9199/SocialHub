package com.social.SocialHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PostLikedEvent {

    private UUID likedByUserId;

    private UUID postOwnerId;

    private UUID postId;
}