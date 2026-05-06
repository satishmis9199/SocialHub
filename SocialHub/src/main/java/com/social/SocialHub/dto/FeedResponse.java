package com.social.SocialHub.dto;

import java.util.List;

public class FeedResponse {

    private List<PostResponse> data;
    private String nextCursor;

    public FeedResponse(List<PostResponse> data, String nextCursor) {
        this.data = data;
        this.nextCursor = nextCursor;
    }

    public List<PostResponse> getData() { return data; }
    public String getNextCursor() { return nextCursor; }
}