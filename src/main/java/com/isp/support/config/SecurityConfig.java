package com.isp.support.config;

import com.isp.support.service.CurrentUserService;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/welcome", "/login", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll().anyRequest().authenticated()).formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/dashboard", true).failureUrl("/login?error").permitAll()).logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }
}
