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
import org.apache.catalina.connector.InputBuffer;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        executorService.submit(() -> executeJob(job));
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

    private void executeJob(Job job) {

        JobExecution execution = createExecution(job);
        markRunning(job);

        try {
            executeCommand(job, execution);
            markSuccess(job, execution);
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            markFailure(job, execution, e);
        }
        finally {
            saveExecution(job, execution);
        }

    }

    private JobExecution createExecution(Job job){
        JobExecution execution = JobExecution.builder()
                .job(job)
                .status(JobStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .build();
        return jobExecutionRepository.save(execution);
    }

    private void markRunning(Job job) {
        job.setStatus(JobStatus.RUNNING);
        jobRepository.save(job);
    }

    private void executeCommand(Job job,JobExecution execution) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(
                "cmd",
                "/c",
                job.getCommand()
        );

        Process process = processBuilder.start();

        String output = readStream(process.getInputStream());
        String error = readStream(process.getErrorStream());

        int exitCode = process.waitFor();
        execution.setExitCode(exitCode);

        if (exitCode != 0) {
            throw new RuntimeException(
                    error.isBlank() ? "Command execution failed." : error
            );
        }
        execution.setOutput(output);
    }

    private void markSuccess(Job job,JobExecution execution){
        job.setStatus(JobStatus.SUCCESS);
        execution.setCompletedAt(LocalDateTime.now());
        execution.setStatus(JobStatus.SUCCESS);
        execution.setExitCode(0);
    }

    private void markFailure(Job job,JobExecution execution,Exception e){
        execution.setStatus(JobStatus.FAILED);
        execution.setCompletedAt(LocalDateTime.now());
        execution.setExitCode(1);
        execution.setErrorMessage(e.getMessage());
        job.setStatus(JobStatus.FAILED);
    }

    private void saveExecution(Job job,JobExecution execution){
        jobRepository.save(job);
        jobExecutionRepository.save(execution);
    }

    private String readStream(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );

        String line;
        StringBuilder output = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return output.toString();
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
