package com.bank.bankapi.service;

import com.bank.bankapi.dto.CreatePhoneCodeRequest;
import com.bank.bankapi.dto.ResetPasswordConfirmRequest;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class PasswordResetService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    public void CreatePhoneCode(CreatePhoneCodeRequest request){

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User with such phone number is not existing"));

        SecureRandom random = new SecureRandom();

        redisTemplate.opsForValue().set(
                "reset:" + request.getPhoneNumber(),
                String.valueOf(100000 + random.nextInt(900000)),
                Duration.ofMinutes(3)
        );

        log.info("Password reset code for {}: {}", request.getPhoneNumber(),
                redisTemplate.opsForValue()
                        .get("reset:" + request.getPhoneNumber()));


    }

    public void confirmReset(ResetPasswordConfirmRequest request){

        String savedCode = redisTemplate.opsForValue()
                .get("reset:" + request.getPhoneNumber());
        if(savedCode == null){
            throw new RuntimeException("Code has expired or was not requested");
        }

        if(!savedCode.equals(request.getCode())){
            throw new RuntimeException("Code is not correct");
        }

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete("reset:" + request.getPhoneNumber());
    }


}
