package com.rentconnect.backend.service;

import com.rentconnect.backend.dto.UserDto;
import com.rentconnect.backend.entity.User;
import com.rentconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(UserDto userDto) {
        // 1. Check if email already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return "Error: Email is already in use!";
        }

        // 2. Map DTO to Entity
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // We will hash this later with Security
        user.setRole(userDto.getRole().toUpperCase());
        user.setPhone(userDto.getPhone());

        // 3. Save to Database
        userRepository.save(user);

        return "User Registered Successfully!";
    }
}