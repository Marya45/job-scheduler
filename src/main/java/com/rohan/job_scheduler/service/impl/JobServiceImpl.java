package com.rohan.job_scheduler.service.impl;

import com.rohan.job_scheduler.dto.request.CreateJobRequest;
import com.rohan.job_scheduler.dto.response.JobResponse;
import com.rohan.job_scheduler.entity.CustomUserPrincipal;
import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobStatus;
import com.rohan.job_scheduler.entity.User;
import com.rohan.job_scheduler.repository.JobRepository;
import com.rohan.job_scheduler.service.AuthenticationService;
import com.rohan.job_scheduler.service.JobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final AuthenticationService authenticationService;

    public JobServiceImpl(JobRepository jobRepository, AuthenticationService authenticationService){
        this.jobRepository = jobRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public JobResponse createJob(CreateJobRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        Job job = Job.builder()
                .name(request.getName())
                .command(request.getCommand())
                .scheduledAt(request.getScheduledAt())
                .status(JobStatus.PENDING)
                .createdBy(currentUser)
                .build();

        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    @Override
    public List<JobResponse> getMyJobs() {
        User currentUser = authenticationService.getCurrentUser();

        List<Job> jobs = jobRepository.findByCreatedBy(currentUser);

        return jobs.stream()
                .map(this::mapToJobResponse)
                .toList();
    }

    @Override
    public JobResponse getJobById(Long id) {
        User currentUser = authenticationService.getCurrentUser();

        Job job = jobRepository.findByIdAndCreatedBy(id,currentUser).orElseThrow(() -> new RuntimeException("No Job found"));

        return mapToJobResponse(job);
    }

    @Override
    public JobResponse updateJob(Long id, CreateJobRequest job) {
        User currentUser = authenticationService.getCurrentUser();

        Job savedJob = jobRepository.findByIdAndCreatedBy(id,currentUser).orElseThrow(() -> new RuntimeException("No Job found"));

        savedJob.setName(job.getName());
        savedJob.setCommand(job.getCommand());
        savedJob.setScheduledAt(job.getScheduledAt());
        jobRepository.save(savedJob);

        return mapToJobResponse(savedJob);
    }

    @Override
    public void deleteJob(Long id) {
        User currentUser = authenticationService.getCurrentUser();
        Job job = jobRepository.findByIdAndCreatedBy(id, currentUser).orElseThrow(() -> new RuntimeException("No Job found"));

        jobRepository.delete(job);
    }

    private JobResponse mapToJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .command(job.getCommand())
                .status(job.getStatus())
                .scheduledAt(job.getScheduledAt())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

}
