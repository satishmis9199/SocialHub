package com.social.SocialHub.config;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;

import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void init(){

        try{

            FileInputStream serviceAccount =

                    new FileInputStream(

                            "src/main/resources/firebase/serviceAccountKey.json"
                    );

            FirebaseOptions options =

                    FirebaseOptions.builder()

                            .setCredentials(

                                    GoogleCredentials
                                            .fromStream(
                                                    serviceAccount
                                            )
                            )

                            .build();

            if(
                    FirebaseApp.getApps().isEmpty()
            ){

                FirebaseApp.initializeApp(options);

                log.error(
                        "FIREBASE INITIALIZED"
                );
            }

        }catch(Exception e){

            e.printStackTrace();

            log.error(
                    "FIREBASE ERROR = {}",
                    e.getMessage()
            );
        }
    }
}