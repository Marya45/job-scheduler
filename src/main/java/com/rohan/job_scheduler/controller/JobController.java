package com.rohan.job_scheduler.controller;

import com.rohan.job_scheduler.dto.request.CreateJobRequest;
import com.rohan.job_scheduler.dto.response.JobResponse;
import com.rohan.job_scheduler.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request){
        JobResponse response = jobService.createJob(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getMyJobs(){
        List<JobResponse> responses = jobService.getMyJobs();
        return ResponseEntity.ok(responses);
    }


}
