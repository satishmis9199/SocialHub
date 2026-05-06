package com.social.SocialHub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class Follow {

    @Id
    @GeneratedValue
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private UserEntity follower;
    private String status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private UserEntity following;

    private LocalDateTime createdAt = LocalDateTime.now();


}