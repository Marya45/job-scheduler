package com.rohan.job_scheduler.service.impl;

import com.rohan.job_scheduler.entity.CustomUserPrincipal;
import com.rohan.job_scheduler.entity.User;
import com.rohan.job_scheduler.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser();
    }
}
