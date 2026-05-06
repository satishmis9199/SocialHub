package com.social.SocialHub.restControlller;

import com.social.SocialHub.service.MailService;
import com.social.SocialHub.dto.EmailRequestDTO;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/send-email")
public class EmailController {

    @Autowired
    private MailService mailService;

    @PostMapping
    public Map<String, Integer> sendMail(@ModelAttribute EmailRequestDTO emailRequestDTO) {
        int count = mailService.sendMail(emailRequestDTO);

        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);

        return response;
    }
}