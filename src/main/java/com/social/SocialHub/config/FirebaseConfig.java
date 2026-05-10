package com.social.SocialHub.config;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;

import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void init(){

        try{

            // ============================================
            // GET JSON FROM ENV VARIABLE
            // ============================================

            String firebaseConfig =

                    System.getenv(
                            "FIREBASE_CONFIG"
                    );

            // ============================================
            // LOCAL FALLBACK
            // ============================================

            if(

                    firebaseConfig == null ||

                            firebaseConfig.isBlank()
            ){

                log.error(
                        "USING LOCAL FIREBASE JSON"
                );

                firebaseConfig = """
{
  "type": "service_account",
  "project_id": "YOUR_PROJECT_ID",
  "private_key_id": "YOUR_PRIVATE_KEY_ID",
  "private_key": "YOUR_PRIVATE_KEY",
  "client_email": "YOUR_CLIENT_EMAIL",
  "client_id": "YOUR_CLIENT_ID",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "YOUR_CERT_URL",
  "universe_domain": "googleapis.com"
}
""";
            }

            // ============================================
            // CREATE INPUT STREAM
            // ============================================

            InputStream serviceAccount =

                    new ByteArrayInputStream(

                            firebaseConfig.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            // ============================================
            // FIREBASE OPTIONS
            // ============================================

            FirebaseOptions options =

                    FirebaseOptions.builder()

                            .setCredentials(

                                    GoogleCredentials
                                            .fromStream(
                                                    serviceAccount
                                            )
                            )

                            .build();

            // ============================================
            // INIT FIREBASE
            // ============================================

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