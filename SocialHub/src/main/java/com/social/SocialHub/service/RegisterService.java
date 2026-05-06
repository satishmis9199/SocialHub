package com.social.SocialHub.service;

import com.social.SocialHub.dto.RegistserDto;

import com.social.SocialHub.entity.Roles;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private static  final Logger logger= LoggerFactory.getLogger(RegisterService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Observed(name = "register-user-service")
    public String registerUser(RegistserDto registserDto){
        try{
            UserEntity user1=userRepository.findByEmailAndUsername(registserDto.getEmail(),registserDto.getUsername());
            if(user1!=null){
                logger.error("Username Or Password Already Exist");
                return "Username Or Email Already Exist....";

            }

            UserEntity user=new UserEntity();
            user.setUsername(registserDto.getUsername());
            user.setRole(Roles.ADMIN);
            user.setEmail(registserDto.getEmail());
            user.setPassword(passwordEncoder.encode(registserDto.getPassword()));
            user.setProvider(registserDto.getProvider());
            user.setProviderId(registserDto.getProviderId());
            userRepository.save(user);
            return "Registered SuccessFully";


        }catch(Exception e){
            return "Error While Registering";
        }
    }
}
