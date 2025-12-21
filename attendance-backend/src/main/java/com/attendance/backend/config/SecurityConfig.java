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
                // CSRF DISABLED (API / ANDROID)
                // ---------------------------------
                .csrf(csrf -> csrf.disable())

                // ---------------------------------
                // AUTHORIZATION RULES
                // ---------------------------------
                .authorizeHttpRequests(auth -> auth

                        // ---------- STATIC / PUBLIC ----------
                        .requestMatchers(
                                "/",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // ---------- AUTH (JWT ISSUED HERE) ----------
                        .requestMatchers(
                                "/api/auth/**",
                                "/auth/student/login",
                                "/api/teacher/login"
                        ).permitAll()

                        // ---------- API (JWT ONLY, NO FORM LOGIN) ----------
                        .requestMatchers("/api/**").authenticated()

                        // ---------- ADMIN ----------
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )

                // ---------------------------------
                // FORM LOGIN (ADMIN ONLY)
                // ---------------------------------
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                // ---------------------------------
                // API EXCEPTION HANDLING (CRITICAL FIX)
                // ---------------------------------
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write(
                                        "{\"message\":\"Unauthorized\"}"
                                );
                            } else {
                                response.sendRedirect("/");
                            }
                        })
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
