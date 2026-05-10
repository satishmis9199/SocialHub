package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.FcmTokenRequest;

import com.social.SocialHub.entity.UserEntity;

import com.social.SocialHub.repository.UserRepository;

import com.social.SocialHub.service.PostService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FirebaseController {

    private final PostService postService;

    private final UserRepository userRepository;

    @PostMapping("/save-fcm-token")
    public void saveFcmToken(

            @RequestBody
            FcmTokenRequest request
    ){

        UserEntity user =

                postService
                        .getLoggedInUser();

        user.setFcmToken(

                request.getToken()
        );

        userRepository.save(user);

        log.error(
                "FCM TOKEN SAVED"
        );
    }
}