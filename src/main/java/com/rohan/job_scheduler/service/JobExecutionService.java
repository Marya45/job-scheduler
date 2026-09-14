package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.dto.response.JobExecutionResponse;
import com.rohan.job_scheduler.entity.Job;
import java.util.List;

public interface JobExecutionService {

    void execute(Long jobId);

    boolean claimJob(Job job);

    List<JobExecutionResponse> getExecutionHistory(Long jobId);

    void executeSynchronously(Long jobId); // RabbitMQ worker use
}
