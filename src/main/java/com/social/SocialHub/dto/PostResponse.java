package com.social.SocialHub.dto;

import com.social.SocialHub.dto.MediaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class PostResponse {
    public PostResponse(UUID postId, String username,UUID userId, String profilePic, String caption, int likeCount, boolean liked, List<MediaDTO> media,int commentCount) {
        this.postId = postId;
        this.username = username;
        this.userId=userId;
        this.profilePic = profilePic;
        this.caption = caption;
        this.likeCount = likeCount;
        this.liked = liked;
        this.media = media;
        this.commentCount=commentCount;

    }

    private UUID postId;
    private String username;
    private UUID userId;
    private String profilePic;
    private String caption;
    private int likeCount;
    private boolean liked;
    private List<MediaDTO> media;
    private int commentCount;

    // constructor
}