package com.rohan.job_scheduler.service.impl;

import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobStatus;
import com.rohan.job_scheduler.repository.JobRepository;
import com.rohan.job_scheduler.service.JobExecutionService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class JobExecutionServiceImpl implements JobExecutionService {

    private final JobRepository jobRepository;
    private final ExecutorService executorService;

    public JobExecutionServiceImpl(JobRepository jobRepository, ExecutorService executorService) {
        this.jobRepository = jobRepository;
        this.executorService = executorService;
    }


    @Override
    public void execute(Job job) {

        executorService.submit(() -> executeInternal(job));

    }

    private void executeInternal(Job job) {

        job.setStatus(JobStatus.RUNNING);
        jobRepository.save(job);

        try {

            Thread.sleep(5000);

            job.setStatus(JobStatus.SUCCESS);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            job.setStatus(JobStatus.FAILED);

        }

        jobRepository.save(job);
    }

}
