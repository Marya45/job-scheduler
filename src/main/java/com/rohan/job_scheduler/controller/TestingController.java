package com.rohan.job_scheduler.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestingController {

    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello " + authentication.getName();
    }

}
