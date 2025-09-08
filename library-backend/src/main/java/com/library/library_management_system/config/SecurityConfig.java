package com.library.library_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/books/search").permitAll()
                .requestMatchers("/api/books/available").permitAll()
                .requestMatchers("/api/books/categories").permitAll()
                .requestMatchers("/api/books/authors").permitAll()
                .requestMatchers("/api/books/{id}").permitAll()
                .requestMatchers("/api/books/{id}/availability").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/users/members").hasRole("ADMIN")
                .requestMatchers("/api/users/{id}/membership/extend").hasRole("ADMIN")
                .requestMatchers("/api/users/expired-memberships").hasRole("ADMIN")
                .requestMatchers("/api/books").hasAnyRole("ADMIN", "MEMBER") // GET all books
                .requestMatchers("/api/books/**").hasRole("ADMIN") // All other book operations
                .requestMatchers("/api/transactions/update-overdue").hasRole("ADMIN")
                .requestMatchers("/api/transactions/statistics").hasRole("ADMIN")
                .requestMatchers("/api/transactions/unpaid-fines").hasRole("ADMIN")
                .requestMatchers("/api/transactions/{id}/waive-fine").hasRole("ADMIN")
                .requestMatchers("/api/reservations/update-expired").hasRole("ADMIN")
                .requestMatchers("/api/reservations/send-notifications").hasRole("ADMIN")
                .requestMatchers("/api/reservations/statistics").hasRole("ADMIN")
                .requestMatchers("/api/reservations/needs-notification").hasRole("ADMIN")
                .requestMatchers("/api/reservations/expired").hasRole("ADMIN")
                
                // Member and Admin endpoints
                .requestMatchers("/api/users/{id}").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/users/search").hasRole("ADMIN")
                .requestMatchers("/api/transactions/borrow").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/transactions/{id}/return").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/transactions/{id}/renew").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/transactions/user/{userId}/**").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/transactions/{id}/pay-fine").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/reservations").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/reservations/user/{userId}/**").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/reservations/{id}/cancel").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/reservations/{id}/fulfill").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/reservations/can-reserve").hasAnyRole("ADMIN", "MEMBER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React development server
            "http://localhost:3001",  // Alternative React port
            "http://127.0.0.1:3000"   // Alternative localhost format
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // How long the browser should cache CORS configuration
        configuration.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}