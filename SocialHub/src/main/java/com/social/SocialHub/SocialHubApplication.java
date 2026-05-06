package com.social.SocialHub;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.TimeZone;
@EnableMethodSecurity
@SpringBootApplication
@EnableScheduling
public class SocialHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialHubApplication.class, args);
    }
//	@PostConstruct
//	public void init() {
//		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
//	}
}
