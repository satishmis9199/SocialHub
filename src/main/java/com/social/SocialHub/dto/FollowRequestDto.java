package com.social.SocialHub.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FollowRequestDto {

    private UUID targetUserId;  // ✅ FIXED
}