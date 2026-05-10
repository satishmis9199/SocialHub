package com.social.SocialHub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID senderId;

    private UUID receiverId;

    @Column(columnDefinition = "TEXT")
    private String message;

    private boolean seen;

    private LocalDateTime createdAt;
}