package com.attendance.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // -----------------------------
                // CSRF (DISABLED FOR ANDROID)
                // -----------------------------
                .csrf(csrf -> csrf.disable())

                // -----------------------------
                // AUTHORIZATION RULES
                // -----------------------------
                .authorizeHttpRequests(auth -> auth

                        // ---------- PUBLIC ----------
                        .requestMatchers(
                                "/",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // ---------- ANDROID APIs ----------
                        .requestMatchers(
                                "/auth/**",
                                "/attendance/**",
                                "/classes/**",
                                "/api/teacher/**"
                        ).permitAll()

                        // ---------- ADMIN ----------
                        .requestMatchers(
                                "/admin/login"
                        ).permitAll()

                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("ADMIN")

                        // ---------- DEFAULT ----------
                        .anyRequest().authenticated()
                )

                // -----------------------------
                // ADMIN LOGIN (THYMELEAF)
                // -----------------------------
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                // -----------------------------
                // LOGOUT
                // -----------------------------
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    // -----------------------------
    // PASSWORD ENCODER
    // -----------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
