package com.attendance.backend;

import com.attendance.backend.model.user.Admin;
import com.attendance.backend.model.user.AdminRole;
import com.attendance.backend.model.user.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
public class AttendanceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceBackendApplication.class, args);
    }


    @Bean
    public CommandLineRunner createAdmin(
            AdminRepository repo,
            PasswordEncoder encoder,
            @Value("${bootstrap.admin.username:}") String username,
            @Value("${bootstrap.admin.password:}") String password
    ) {

        return args -> {


            if (repo.count() > 0) {
                return;
            }


            if (username == null || username.isBlank()
                    || password == null || password.isBlank()) {

                System.out.println("Super admin bootstrap skipped (no env config)");
                return;
            }


            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(encoder.encode(password));
            admin.setRole(AdminRole.ADMIN);
            admin.setEnabled(true);

            repo.save(admin);

            System.out.println("Super admin created from environment variables");
        };
    }
}
