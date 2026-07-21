package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobStatus;
import com.rohan.job_scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SchedulerService {

    private final JobRepository jobRepository;
    private final JobExecutionService jobExecutionService;

    public SchedulerService(JobRepository jobRepository, JobExecutionService jobExecutionService) {
        this.jobRepository = jobRepository;
        this.jobExecutionService = jobExecutionService;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollJobs() {
        log.info("Scheduler polling for pending jobs...");

        LocalDateTime now = LocalDateTime.now();

        List<Job> jobs = jobRepository.findByStatusAndScheduledAtLessThanEqual(JobStatus.PENDING,now);

        System.out.println("Found " + jobs.size() + "jobs ready to execute");

        log.info("Found {} jobs ready to execute", jobs.size());

        for(Job job: jobs){
            log.info("Submitting job {} for execution", job.getId());
            if(jobExecutionService.claimJob(job)){
                jobExecutionService.execute(job);
            }
        }

    }
}
