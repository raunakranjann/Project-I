package com.attendance.backend;

import com.attendance.backend.entity.Admin;
import com.attendance.backend.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AttendanceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner createAdmin(
            AdminRepository repo,
            PasswordEncoder encoder) {

        return args -> {
            if (repo.count() == 0) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                repo.save(admin);
            }
        };
    }
}
