package com.social.SocialHub.dto;

import lombok.Builder;

import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SeenMessageResponse {

    private UUID senderId;

    private UUID receiverId;

    private boolean seen;
}