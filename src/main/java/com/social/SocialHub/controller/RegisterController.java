package com.social.SocialHub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api")
public class RegisterController {
    @GetMapping("/register")
    public String registerUser(){
        return "Registration";
    }
    @GetMapping("/forget")
    public String forgetUser(){
        return "forget";
    }
}
