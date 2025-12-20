package com.attendance.backend.controller;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;

import com.attendance.backend.entity.User;
import com.attendance.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String rollNo,
            @RequestParam String password,
            @RequestParam String deviceId
    ) {
        return userRepository.findByRollNo(rollNo)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {

                    if (user.getDeviceId() == null) {
                        user.setDeviceId(deviceId);
                        userRepository.save(user);
                    }

                    Map<String, String> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("message", "LOGIN SUCCESS");

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("status", "FAILED");
                    response.put("message", "LOGIN FAILED");
                    return ResponseEntity.status(401).body(response);
                });
    }

}
