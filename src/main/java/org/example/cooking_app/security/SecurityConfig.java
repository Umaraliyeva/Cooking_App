package org.example.cooking_app.security;

import org.example.cooking_app.filter.MyFilter;
import org.example.cooking_app.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;


    public SecurityConfig(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomUserDetailService customUserDetailService, MyFilter myFilter) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(requests ->
                requests
                        .requestMatchers( "api/auth/**","/superAdmin/user/**","/role","/user","/user/**","/frontend/userCRUD.html", "/frontend/login.html","http://localhost:8080/file/**","/file/**", "/market/**", "/product/**", "/category/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/frontend/index.html").hasRole("ADMIN")
                        .requestMatchers("/frontend/editUser.html").permitAll()
                        .requestMatchers("/frontend/addUser.html").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        .anyRequest().authenticated());
        http.userDetailsService(customUserDetailService);
        http.addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Adjust as needed
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider(
                passwordEncoder()
        );
        authenticationProvider.setUserDetailsService(customUserDetailService);
        return authenticationProvider;
    }


   @Bean
    public AuthenticationManager authenticationManager() {
         return new ProviderManager(authenticationProvider());
    }




}
