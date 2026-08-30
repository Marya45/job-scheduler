package com.rohan.job_scheduler.queue;

public interface JobQueue {

    void submit(Long jobId);
}
