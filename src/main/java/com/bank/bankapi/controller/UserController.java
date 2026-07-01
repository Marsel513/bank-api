package com.bank.bankapi.controller;

import com.bank.bankapi.dto.PinChangeRequest;
import com.bank.bankapi.entity.User;
import com.bank.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PatchMapping("/change/pin")
    public ResponseEntity<String> changePin(@RequestBody @Valid PinChangeRequest request){
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        userService.changeUserPin(request, user);
        return ResponseEntity.status(200).body("Pin is successfully changed");
    }
}
