package com.social.SocialHub.dto;

public class LikeResponse {

    private boolean liked;
    private int likeCount;

    public LikeResponse(boolean liked, int likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public boolean isLiked() { return liked; }
    public int getLikeCount() { return likeCount; }
}