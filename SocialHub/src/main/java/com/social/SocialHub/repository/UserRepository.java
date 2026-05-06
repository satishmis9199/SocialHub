package com.social.SocialHub.repository;

import com.social.SocialHub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmailAndUsername(String email, String username);

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
}
