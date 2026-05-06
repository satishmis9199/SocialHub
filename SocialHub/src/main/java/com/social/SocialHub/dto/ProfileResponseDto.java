package com.social.SocialHub.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProfileResponseDto {

    private UUID id;

    private String username;
    private String handle;        // ✅ add
    private String email;
    private String displayName;

    private String bio;
    private String profilePic;
    private String coverImage;

    private String location;      // ✅ add
    private String website;       // ✅ add
    private LocalDateTime createdAt; // ✅ add

    private Long followersCount;
    private Long followingCount;
    private Long postCount;

    private boolean isPrivate;
    private boolean isVerified;
    private boolean isCurrentUser;
    private Long totalLikes;
    private Long totalComments;
    private Long profileViews;
}