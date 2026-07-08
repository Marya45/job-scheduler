package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.dto.request.LoginRequest;
import com.rohan.job_scheduler.dto.request.RegisterRequest;
import com.rohan.job_scheduler.dto.response.AuthResponse;

public interface UserService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
