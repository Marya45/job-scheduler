package com.rohan.job_scheduler.service.impl;

import com.rohan.job_scheduler.dto.response.JobExecutionResponse;
import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobExecution;
import com.rohan.job_scheduler.entity.JobStatus;
import com.rohan.job_scheduler.entity.User;
import com.rohan.job_scheduler.repository.JobExecutionRepository;
import com.rohan.job_scheduler.repository.JobRepository;
import com.rohan.job_scheduler.service.AuthenticationService;
import com.rohan.job_scheduler.service.JobExecutionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class JobExecutionServiceImpl implements JobExecutionService {

    private final JobRepository jobRepository;
    private final ExecutorService executorService;
    private final JobExecutionRepository jobExecutionRepository;
    private final AuthenticationService authenticationService;

    public JobExecutionServiceImpl(JobRepository jobRepository, ExecutorService executorService, JobExecutionRepository jobExecutionRepository, AuthenticationService authenticationService) {
        this.jobRepository = jobRepository;
        this.executorService = executorService;
        this.jobExecutionRepository = jobExecutionRepository;
        this.authenticationService = authenticationService;
    }


    @Override
    public void execute(Job job) {

        executorService.submit(() -> executeInternal(job));

    }

    @Override
    public boolean claimJob(Job job) {
        if (job.getStatus() != JobStatus.PENDING) {
            return false;
        }

        job.setStatus(JobStatus.RUNNING);
        jobRepository.save(job);

        return true;
    }

    @Override
    public List<JobExecutionResponse> getExecutionHistory(Long jobId) {
        User currentUser = authenticationService.getCurrentUser();
        Job job = jobRepository.findByIdAndCreatedBy(jobId, currentUser).orElseThrow(() -> new RuntimeException("No Job found"));

        List<JobExecution> executions = jobExecutionRepository.findByJobOrderByStartedAtDesc(job);

        return executions.stream()
                .map(this::mapToJobExecutionResponse)
                .toList();
    }

    private void executeInternal(Job job) {

        JobExecution execution = JobExecution.builder()
                .job(job)
                .status(JobStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .build();

        jobExecutionRepository.save(execution);

        job.setStatus(JobStatus.RUNNING);
        jobRepository.save(job);

        try {

            Thread.sleep(5000);

            job.setStatus(JobStatus.SUCCESS);
            execution.setStatus(JobStatus.SUCCESS);
            execution.setExitCode(0);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            execution.setStatus(JobStatus.FAILED);
            execution.setExitCode(1);
            execution.setErrorMessage(e.getMessage());
            job.setStatus(JobStatus.FAILED);

        }
        finally {

            execution.setCompletedAt(LocalDateTime.now());

            jobRepository.save(job);
            jobExecutionRepository.save(execution);
        }

    }

    private JobExecutionResponse mapToJobExecutionResponse(JobExecution execution){
        return JobExecutionResponse
                .builder()
                .id(execution.getId())
                .status(execution.getStatus())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .exitCode(execution.getExitCode())
                .output(execution.getOutput())
                .errorMessage(execution.getErrorMessage())
                .build();
    }

}
