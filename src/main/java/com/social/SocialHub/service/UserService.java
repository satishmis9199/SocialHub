package com.social.SocialHub.service;


import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean resetPassword(String email,
                                 String newPassword) {

        UserEntity user =
                userRepository.findByEmail(email);

        if (user == null) {
            return false;
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        return true;
    }
}