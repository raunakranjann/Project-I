package com.attendance.backend.config;

import com.attendance.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ---------------------------------
                // CSRF DISABLED (ANDROID / API)
                // ---------------------------------
                .csrf(csrf -> csrf.disable())

                // ---------------------------------
                // AUTHORIZATION RULES
                // ---------------------------------
                .authorizeHttpRequests(auth -> auth

                        // ---------- PUBLIC ----------
                        .requestMatchers(
                                "/",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // ---------- LOGIN (JWT ISSUED) ----------
                        .requestMatchers(
                                "/auth/student/login",
                                "/api/teacher/login"
                        ).permitAll()

                        // ---------- STUDENT (JWT + ROLE_STUDENT) ----------
                        .requestMatchers(
                                "/attendance/**",
                                "/classes/**"
                        ).hasRole("STUDENT")

                        // ---------- TEACHER (JWT + ROLE_TEACHER) ----------
                        .requestMatchers(
                                "/api/teacher/**"
                        ).hasRole("TEACHER")

                        // ---------- ADMIN ----------
                        .requestMatchers(
                                "/",
                                "/admin/login"
                        ).permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ---------- FALLBACK ----------
                        .anyRequest().authenticated()
                )

                // ---------------------------------
                // ADMIN FORM LOGIN (THYMELEAF)
                // ---------------------------------
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                // ---------------------------------
                // ADMIN LOGOUT
                // ---------------------------------
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .permitAll()
                )

                // ---------------------------------
                // JWT FILTER
                // ---------------------------------
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ---------------------------------
    // PASSWORD ENCODER
    // ---------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
