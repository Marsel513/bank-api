package com.bank.bankapi.service;

import com.bank.bankapi.dto.LoginRequest;
import com.bank.bankapi.dto.LoginResponse;
import com.bank.bankapi.dto.RegisterRequest;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.repository.UserRepository;
import com.bank.bankapi.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest){
        User user = new User();

        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPin(registerRequest.getPin());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User is not found"));

        if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            LoginResponse loginResponse = new LoginResponse(jwtService
                    .generateToken(user
                    .getPhoneNumber()));
            return loginResponse;
        }
        else {
            throw  new RuntimeException("Password is not correct");
        }

    }
}
