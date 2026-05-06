package com.social.SocialHub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDto {

    private boolean following;
    private boolean requested;
    private String message;
    private String followStatus;

    public FollowResponseDto(boolean following, boolean requested, String message,String followStatus) {
        this.following = following;
        this.requested = requested;
        this.message = message;
        this.followStatus=followStatus;
    }

   
}