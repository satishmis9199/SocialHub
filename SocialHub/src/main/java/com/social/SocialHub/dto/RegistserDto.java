package com.social.SocialHub.dto;

import com.social.SocialHub.entity.AuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class RegistserDto {
    private String username;
    private String  email;
    private String  password;
    private AuthProvider provider;
    private String providerId;
}
