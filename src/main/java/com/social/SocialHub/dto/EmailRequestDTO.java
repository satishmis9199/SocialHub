package com.social.SocialHub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@Getter
@Setter
public class EmailRequestDTO {

    private String emails;   // multiple emails
    private String subject;
    private String body;
    private MultipartFile file;    // attachment
}