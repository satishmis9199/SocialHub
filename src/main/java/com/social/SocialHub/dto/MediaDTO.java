package com.social.SocialHub.dto;

// package: com.social.SocialHub.dto
public class MediaDTO {
    private String url;
    private String type; // IMAGE / VIDEO

    public MediaDTO(String url, String type) {
        this.url = url;
        this.type = type;
    }
    public String getUrl() { return url; }
    public String getType() { return type; }
}