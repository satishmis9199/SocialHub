package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.ChatMessageResponse;
import com.social.SocialHub.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{userId}")
    public List<ChatMessageResponse>
    getMessages(
            @PathVariable UUID userId
    ) {
        log.error("Inside getMessagee");

        return chatService
                .getMessages(userId);
    }
    @PutMapping("/seen/{senderId}")
    public void markAsSeen(@PathVariable UUID senderId){
        chatService.markAssSeen(senderId);
    }
}