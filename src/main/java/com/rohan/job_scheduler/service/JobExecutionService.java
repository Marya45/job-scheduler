package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.entity.Job;

public interface JobExecutionService {

    void execute(Job job);

    boolean claimJob(Job job);
}
