package com.social.SocialHub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FollowersResponseDto {

    private UUID id;
    private String username;
    private String handle;

    @JsonProperty("isFollowing")
    private boolean following;
    private String followStatus;
    public FollowersResponseDto(UUID id, String username, String handle, boolean following,String status) {
        this.id = id;
        this.username = username;
        this.handle = handle;
        this.following = following;
        this.followStatus=status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getStatus() {
        return followStatus;
    }

    public void setStatus(String status) {
        this.followStatus = status;
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getHandle() { return handle; }

    public boolean isFollowing() {
        return following;
    }
}