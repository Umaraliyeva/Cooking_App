package org.example.cooking_app.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cooking_app.entity.User;
import org.example.cooking_app.repo.UserRepository;
import org.example.cooking_app.service.CustomUserDetailService;
import org.example.cooking_app.service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MyFilter extends OncePerRequestFilter {


    private final TokenService tokenService;
    private final CustomUserDetailService customUserDetailService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7).trim();

        if (tokenService.isValid(token)) {
            String userName = tokenService.getUserName(token);
            List<SimpleGrantedAuthority> roles = tokenService.getRoles(token);
            UserDetails userDetails = customUserDetailService.loadUserByUsername(userName);

            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, roles);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);

    }
}
