package com.rohan.job_scheduler.controller;

import com.rohan.job_scheduler.dto.request.LoginRequest;
import com.rohan.job_scheduler.dto.request.RegisterRequest;
import com.rohan.job_scheduler.dto.response.AuthResponse;
import com.rohan.job_scheduler.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){

        userService.register(request);

        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        AuthResponse res = userService.login(request);
        return ResponseEntity.ok(res);
    }


}
