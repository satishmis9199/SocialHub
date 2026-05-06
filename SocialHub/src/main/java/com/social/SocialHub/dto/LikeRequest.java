package com.social.SocialHub.dto;

import java.util.UUID;

public class LikeRequest {
    private UUID postId;

    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
}