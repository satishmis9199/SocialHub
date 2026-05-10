package com.social.SocialHub.config;

import com.social.SocialHub.controller.OnlineUsers;
import com.social.SocialHub.entity.UserEntity;

import com.social.SocialHub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionConnectEvent;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEvents {

    private final UserRepository userRepository;

    private final SimpMessagingTemplate
            messagingTemplate;

    @EventListener
    public void handleConnect(
            SessionConnectEvent event
    ) {

        log.error(
                "INSIDE SOCKET CONNECT EVENT"
        );

        StompHeaderAccessor sha =

                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        if(
                sha.getUser() == null
        ){

            log.error(
                    "SOCKET USER NULL"
            );

            return;
        }

        String username =

                sha.getUser()
                        .getName();

        log.error(
                "CONNECTED USERNAME = {}",
                username
        );

        OnlineUsers.ONLINE_USERS
                .add(username);

        log.error(
                "ONLINE USERS SIZE = {}",
                OnlineUsers.ONLINE_USERS.size()
        );

        log.error(
                "CURRENT ONLINE USERS = {}",
                OnlineUsers.ONLINE_USERS
        );

        messagingTemplate
                .convertAndSend(

                        "/topic/online-users",

                        OnlineUsers.ONLINE_USERS
                );

        log.error(
                "ONLINE USERS BROADCASTED"
        );
    }

    @EventListener
    public void handleDisconnect(
            SessionDisconnectEvent event
    ) {

        log.error(
                "INSIDE SOCKET DISCONNECT EVENT"
        );

        StompHeaderAccessor sha =

                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        if(
                sha.getUser() == null
        ){

            log.error(
                    "DISCONNECT USER NULL"
            );

            return;
        }

        String username =

                sha.getUser()
                        .getName();

        log.error(
                "DISCONNECTED USERNAME = {}",
                username
        );

        OnlineUsers.ONLINE_USERS
                .remove(username);

        log.error(
                "ONLINE USERS AFTER REMOVE = {}",
                OnlineUsers.ONLINE_USERS
        );

        UserEntity user =

                userRepository
                        .findByUsername(
                                username
                        );


        if(user != null){

            log.error(
                    "USER FOUND FOR LAST SEEN UPDATE"
            );

            user.setLastSeen(
                    LocalDateTime.now()
            );

            userRepository.save(user);

            log.error(
                    "LAST SEEN UPDATED FOR = {}",
                    username
            );

        }else{

            log.error(
                    "USER NOT FOUND FOR USERNAME = {}",
                    username
            );
        }

        messagingTemplate
                .convertAndSend(

                        "/topic/online-users",

                        OnlineUsers.ONLINE_USERS
                );

        log.error(
                "ONLINE USERS BROADCASTED AFTER DISCONNECT"
        );
    }
}