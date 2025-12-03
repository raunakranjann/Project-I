package com.rentconnect.backend.service;

import com.rentconnect.backend.dto.UserDto;
import com.rentconnect.backend.entity.User;
import com.rentconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create an encoder instance
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return "Error: Email is already in use!";
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        // CHANGE HERE: Encrypt the password!
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setRole(userDto.getRole().toUpperCase());
        user.setPhone(userDto.getPhone());

        userRepository.save(user);
        return "User Registered Successfully!";
    }
}