package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.dto.request.RegisterRequest;

public interface UserService {

    void register(RegisterRequest request);
}
