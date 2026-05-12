package com.social.SocialHub.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {

    private String email;
    private String newPassword;
}