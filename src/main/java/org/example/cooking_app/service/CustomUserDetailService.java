package org.example.cooking_app.service;

import lombok.RequiredArgsConstructor;
import org.example.cooking_app.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) {
     return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
