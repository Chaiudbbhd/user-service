package com.lpk.userservice.controller;

import com.lpk.userservice.model.User;
import com.lpk.userservice.security.JwtTokenProvider;
import com.lpk.userservice.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    Optional<User> existingUserOpt = userService.findByEmail(user.getEmail());

    if (existingUserOpt.isEmpty()) {
        return ResponseEntity.status(401).body("Invalid email");
    }

    User existingUser = existingUserOpt.get();

    if (!userService.passwordMatches(user.getPassword(), existingUser.getPassword())) {
        return ResponseEntity.status(401).body("Invalid password");
    }

    String token = tokenProvider.generateToken(existingUser.getEmail());
    return ResponseEntity.ok().body(token);
}


}
