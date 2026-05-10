package com.social.SocialHub.service;

import com.social.SocialHub.dto.ChatMessageResponse;

import com.social.SocialHub.dto.SeenMessageResponse;

import com.social.SocialHub.entity.ChatMessage;

import com.social.SocialHub.entity.UserEntity;

import com.social.SocialHub.repository.ChatMessageRepository;

import com.social.SocialHub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository
            chatRepo;

    private final UserRepository
            userRepository;

    private final PostService
            postService;

    private final SimpMessagingTemplate
            messagingTemplate;

    // ─────────────────────────────
    // GET CHAT HISTORY
    // ─────────────────────────────

    public List<ChatMessageResponse>
    getMessages(
            UUID otherUserId
    ) {

        UserEntity currentUser =

                postService
                        .getLoggedInUser();

        log.error(
                "CURRENT USER IN MESSAGE = {}",
                currentUser.getUsername()
        );

        List<ChatMessage> messages =

                chatRepo.getChatMessages(

                        currentUser.getId(),

                        otherUserId
                );

        log.error(
                "TOTAL CHAT MESSAGES = {}",
                messages.size()
        );

        return messages.stream()

                .map(msg -> {

                    UserEntity sender =

                            userRepository
                                    .findById(
                                            msg.getSenderId()
                                    )
                                    .orElseThrow();

                    return ChatMessageResponse
                            .builder()

                            .id(
                                    msg.getId()
                            )

                            .senderId(
                                    msg.getSenderId()
                            )

                            .senderUsername(
                                    sender.getUsername()
                            )

                            .receiverId(
                                    msg.getReceiverId()
                            )

                            .message(
                                    msg.getMessage()
                            )

                            .seen(
                                    msg.isSeen()
                            )

                            .createdAt(
                                    msg.getCreatedAt()
                            )

                            .build();
                })

                .toList();
    }

    // ─────────────────────────────
    // MARK AS SEEN
    // ─────────────────────────────

    public void markAssSeen(
            UUID senderId
    ) {

        log.error(
                "INSIDE MARK AS SEEN SERVICE"
        );

        UserEntity currentUser =

                postService
                        .getLoggedInUser();

        log.error(
                "CURRENT USER = {}",
                currentUser.getUsername()
        );

        UserEntity sender =

                userRepository
                        .findById(
                                senderId
                        )
                        .orElseThrow();

        log.error(
                "SENDER USER = {}",
                sender.getUsername()
        );

        // ─────────────────────────────
        // UPDATE DB
        // ─────────────────────────────

        chatRepo.markMessagesSeen(

                senderId,

                currentUser.getId()
        );

        log.error(
                "MESSAGES MARKED AS SEEN"
        );

        // ─────────────────────────────
        // SEND REALTIME SEEN EVENT
        // ─────────────────────────────

        SeenMessageResponse response =

                SeenMessageResponse
                        .builder()

                        .senderId(
                                senderId
                        )

                        .receiverId(
                                currentUser.getId()
                        )

                        .seen(true)

                        .build();

        messagingTemplate
                .convertAndSendToUser(

                        sender.getUsername(),

                        "/queue/seen",

                        response
                );

        log.error(
                "SEEN EVENT SENT TO = {}",
                sender.getUsername()
        );
    }
}