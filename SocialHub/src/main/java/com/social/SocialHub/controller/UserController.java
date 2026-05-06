package com.social.SocialHub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // templates/dashboard.html
    }
    @GetMapping("/create-post")
    public String createPost(){
        return "cretae-post";
    }
    @GetMapping("/profile/{userId}")
    public String getUserProfile(@PathVariable("userId") String userId, Model model) {
        logger.error("Inside getUserProfil hit page");
        logger.error("UserId  Is {}",userId);
        model.addAttribute("userId", userId);

        return "user-profile";
    }
    @GetMapping("/myProfile")
    public String getMyProfile(){
        return "my-profile";
    }
}
