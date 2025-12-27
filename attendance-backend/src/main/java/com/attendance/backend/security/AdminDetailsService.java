package com.attendance.backend.security;

import com.attendance.backend.model.user.Admin;
import com.attendance.backend.model.user.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepo;

    public AdminDetailsService(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles(admin.getRole().name()) // 🔥 ADMIN or ADMINISTRATION
                .disabled(!admin.isEnabled())
                .build();
    }
}

