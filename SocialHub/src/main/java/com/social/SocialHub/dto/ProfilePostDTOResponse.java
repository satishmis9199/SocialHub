package com.social.SocialHub.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Builder
@Data
public class ProfilePostDTOResponse {

    private UUID id;
    private String thumbnail;
    private Long likesCount;
    private Long commentsCount;
}
