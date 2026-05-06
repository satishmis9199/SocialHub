package com.social.SocialHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponse {
    private boolean success;

    private String username;
    private String profilePic;
    private String bio;

    private int followersCount;
    private int followingCount;
    private int postsCount;

    private String followStatus; // FOLLOWING / REQUESTED / NOT_FOLLOWING

    private boolean isPrivate;
    private boolean canViewPosts;

    private List<PostResponse> posts;



}