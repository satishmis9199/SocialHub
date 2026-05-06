package com.social.SocialHub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaUploadResponse {

    private String fileName;
    private String url;
    private String type;
    private long size;

    public MediaUploadResponse(String fileName, String url, String type, long size) {
        this.fileName = fileName;
        this.url = url;
        this.type = type;
        this.size = size;
    }
}