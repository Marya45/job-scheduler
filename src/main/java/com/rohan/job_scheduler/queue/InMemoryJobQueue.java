package com.rohan.job_scheduler.queue;


import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class InMemoryJobQueue implements JobQueue{

    private final BlockingQueue<Long> queue = new LinkedBlockingQueue<>();

    @Override
    public void submit(Long jobId) {
        queue.offer(jobId);
    }

    public Long take() throws InterruptedException {
        return queue.take();
    }

}
