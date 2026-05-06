package com.social.SocialHub.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts",
        indexes = @Index(name = "idx_post_user", columnList = "user_id"))
@Getter
@Setter
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(length = 2000)
    private String caption;

    // 🔥 performance counters
    private int likeCount = 0;
    private int commentCount = 0;
}