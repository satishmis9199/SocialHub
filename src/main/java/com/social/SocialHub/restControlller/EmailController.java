package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.EmailRequestDTO;
import com.social.SocialHub.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/send-email")
public class EmailController {

    private static final Logger log =
            LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private MailService mailService;

    @PostMapping
    public Map<String, Object> sendMail(@ModelAttribute EmailRequestDTO emailRequestDTO) {

        Map<String, Object> response = new HashMap<>();

        try {

            log.info("=== API HIT ===");
            log.info("Emails: {}", emailRequestDTO.getEmails());
            log.info("Subject: {}", emailRequestDTO.getSubject());

            int count = mailService.sendMail(emailRequestDTO);

            log.info("MAIL SENT SUCCESS");

            response.put("count", count);

        } catch (Exception e) {

            log.error("ERROR WHILE SENDING MAIL", e);

            response.put("count", -1);
            response.put("error", e.getMessage());
        }

        return response;
    }
}