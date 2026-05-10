package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.ChatMessageRequest;
import com.social.SocialHub.dto.ChatMessageResponse;
import com.social.SocialHub.entity.ChatMessage;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.ChatMessageRepository;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.FirebaseService;
import com.social.SocialHub.controller.OnlineUsers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatSocketController {

    private final UserRepository userRepository;

    private final ChatMessageRepository chatRepo;

    private final SimpMessagingTemplate messagingTemplate;

    private final FirebaseService firebaseService;

    @MessageMapping("/chat.send")
    public void sendMessage(

            ChatMessageRequest request,

            Principal principal
    ) {

        log.error(
                "INSIDE CHAT WEBSOCKET"
        );

        // ============================================
        // GET SENDER
        // ============================================

        UserEntity sender =

                userRepository
                        .findByUsername(
                                principal.getName()
                        );

        // ============================================
        // GET RECEIVER
        // ============================================

        UserEntity receiver =

                userRepository
                        .findById(
                                request.getReceiverId()
                        )
                        .orElseThrow();

        // ============================================
        // SAVE MESSAGE
        // ============================================

        ChatMessage msg =

                ChatMessage.builder()

                        .senderId(
                                sender.getId()
                        )

                        .receiverId(
                                receiver.getId()
                        )

                        .message(
                                request.getMessage()
                        )

                        .seen(false)

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();

        ChatMessage saved =
                chatRepo.save(msg);

        // ============================================
        // RESPONSE DTO
        // ============================================

        ChatMessageResponse response =

                ChatMessageResponse
                        .builder()

                        .id(
                                saved.getId()
                        )

                        .senderId(
                                sender.getId()
                        )

                        .senderUsername(
                                sender.getUsername()
                        )

                        .receiverId(
                                receiver.getId()
                        )

                        .message(
                                saved.getMessage()
                        )

                        .seen(
                                saved.isSeen()
                        )

                        .createdAt(
                                saved.getCreatedAt()
                        )

                        .build();

        // ============================================
        // REALTIME WEBSOCKET MESSAGE
        // ============================================

        messagingTemplate
                .convertAndSendToUser(

                        receiver.getUsername(),

                        "/queue/messages",

                        response
                );

        log.error(
                "WEBSOCKET MESSAGE SENT TO = {}",
                receiver.getUsername()
        );

        // ============================================
        // FIREBASE PUSH NOTIFICATION
        // ONLY IF USER OFFLINE
        // ============================================

        if(

                !OnlineUsers
                        .ONLINE_USERS
                        .contains(
                                receiver.getUsername()
                        )

                        &&

                        receiver.getFcmToken() != null
        ){

            firebaseService.sendPushNotification(

                    receiver.getFcmToken(),

                    sender.getUsername(),
                    sender.getUsername()+ " sent u a message :"+request.getMessage()
            );

            log.error(
                    "FIREBASE PUSH SENT TO = {}",
                    receiver.getUsername()
            );
        }
    }
}