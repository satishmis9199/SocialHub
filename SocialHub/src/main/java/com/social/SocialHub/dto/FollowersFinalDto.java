package com.social.SocialHub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FollowersFinalDto {

    private int count;

    @JsonProperty("users")
    private List<FollowersResponseDto> users;

    public FollowersFinalDto(int count, List<FollowersResponseDto> users) {
        this.count = count;
        this.users = users;
    }

    public int getCount() {
        return count;
    }

    public List<FollowersResponseDto> getUsers() {
        return users;
    }
}