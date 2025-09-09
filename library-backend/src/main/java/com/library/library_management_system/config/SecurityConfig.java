// src/main/java/com/library/library_management_system/config/SecurityConfig.java
package com.library.library_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/books/**").permitAll()
                .requestMatchers("/api/books/search").permitAll()
                .requestMatchers("/api/books/available").permitAll()
                .requestMatchers("/api/books/categories").permitAll()
                .requestMatchers("/api/books/authors").permitAll()
                .requestMatchers("/api/books/{id}").permitAll()
                .requestMatchers("/api/books/{id}/availability").permitAll()
                .requestMatchers("/api/transactions/**").permitAll()
                .requestMatchers("/api/reservations/**").permitAll()
                .requestMatchers("/api/users/{id}").permitAll()
                .requestMatchers("/api/users/username/{username}").permitAll()
                .requestMatchers("/api/users/search").permitAll()
                .requestMatchers("/api/users/members").hasRole("ADMIN")
                .requestMatchers("/api/users/{id}/membership/extend").hasRole("ADMIN")
                .requestMatchers("/api/users/expired-memberships").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://127.0.0.1:3000"
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}