package com.bank.bankapi.security;

import com.bank.bankapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumer) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumer)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }



}
