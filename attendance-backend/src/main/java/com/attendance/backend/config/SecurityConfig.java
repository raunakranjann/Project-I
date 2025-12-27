package com.attendance.backend.config;

import com.attendance.backend.security.AdminDetailsService;
import com.attendance.backend.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AdminDetailsService adminDetailsService;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            AdminDetailsService adminDetailsService
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.adminDetailsService = adminDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ================= CSRF =================
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/auth/**",
                                "/api/**",
                                "/attendance/**",
                                "/classes/**"
                        )
                )

                // ================= CORS =================
                .cors(Customizer.withDefaults())

                // ================= SESSION =================
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // ================= AUTHORIZATION =================
                .authorizeHttpRequests(auth -> auth

                        // -------- PUBLIC --------
                        .requestMatchers(
                                "/",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/auth/student/login",
                                "/api/teacher/login"
                        ).permitAll()

                        // -------- ADMIN WEB --------
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // -------- ADMINISTRATION WEB --------
                        .requestMatchers("/administration/**")
                        .hasRole("ADMINISTRATION")

                        // -------- ADMIN API --------
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // -------- TEACHER API --------
                        .requestMatchers("/api/teacher/**")
                        .hasRole("TEACHER")

                        // -------- STUDENT API --------
                        .requestMatchers("/attendance/**", "/classes/**")
                        .hasRole("STUDENT")

                        .anyRequest().authenticated()
                )

                // ================= FORM LOGIN =================
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .successHandler((request, response, authentication) -> {

                            boolean isAdmin = authentication.getAuthorities()
                                    .stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/administration/dashboard");
                            }
                        })
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                // ================= LOGOUT =================
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )

                // ================= USER DETAILS =================
                .userDetailsService(adminDetailsService)

                // ================= JWT FILTER =================
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // ================= EXCEPTION HANDLING =================
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {

                            String uri = request.getRequestURI();

                            if (uri.startsWith("/api")
                                    || uri.startsWith("/auth")
                                    || uri.startsWith("/attendance")
                                    || uri.startsWith("/classes")) {

                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter()
                                        .write("{\"message\":\"Unauthorized\"}");
                            } else {
                                response.sendRedirect("/admin/login");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
