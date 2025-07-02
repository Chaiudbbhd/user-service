package com.lpk.userservice.controller;

import com.lpk.userservice.model.User;
import com.lpk.userservice.security.JwtTokenProvider;
import com.lpk.userservice.service.UserService;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;  

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    try {
        System.out.println("Login attempt for email: " + user.getEmail());
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            boolean matches = passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword());
            System.out.println("Password match: " + matches);
            if (matches) {
                String token = tokenProvider.generateToken(existingUser.get().getEmail());
                return ResponseEntity.ok(Collections.singletonMap("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid credentials"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body(Collections.singletonMap("error", "An error occurred: " + e.getMessage()));
    }
}

}
