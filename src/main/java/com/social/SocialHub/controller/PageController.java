package com.social.SocialHub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";   // templates/login.html
    }
    @GetMapping("/validate")
    public ResponseEntity<?> validate() {
        return ResponseEntity.ok("valid");
    }
    @GetMapping("/access-denied")
    public String getAcesDenied(){
        return "access-denied";
    }

}