package com.social.SocialHub.service;

import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.messaging.Message;

import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {

    public void sendPushNotification(

            String token,

            String title,

            String body
    ){

        try{

            Message message =

                    Message.builder()

                            .setToken(token)

                            .setNotification(

                                    Notification.builder()

                                            .setTitle(title)

                                            .setBody(body)

                                            .build()
                            )

                            .build();

            String response =

                    FirebaseMessaging
                            .getInstance()
                            .send(message);

            log.error(
                    "PUSH SENT = {}",
                    response
            );

        }catch(Exception e){

            e.printStackTrace();

            log.error(
                    "PUSH ERROR = {}",
                    e.getMessage()
            );
        }
    }
}