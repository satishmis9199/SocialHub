package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.ChatMessageRequest;
import com.social.SocialHub.dto.ChatMessageResponse;
import com.social.SocialHub.entity.ChatMessage;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.ChatMessageRepository;
import com.social.SocialHub.repository.UserRepository;
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

    @MessageMapping("/chat.send")
    public void sendMessage(

            ChatMessageRequest request,

            Principal principal
    ) {
        log.error("Inside chatWebsocket");

        UserEntity sender =
                userRepository
                        .findByUsername(
                                principal.getName()
                        );


        UserEntity receiver =
                userRepository
                        .findById(
                                request.getReceiverId()
                        )
                        .orElseThrow();

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

        messagingTemplate
                .convertAndSendToUser(

                        receiver.getUsername(),

                        "/queue/messages",

                        response
                );

        log.error(
                "MESSAGE SENT TO {}",
                receiver.getUsername()
        );
    }
}