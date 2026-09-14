package com.rohan.job_scheduler.worker;


import com.rohan.job_scheduler.config.RabbitMQConfig;
import com.rohan.job_scheduler.queue.InMemoryJobQueue;
import com.rohan.job_scheduler.service.JobExecutionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobWorker {
//    private final InMemoryJobQueue jobQueue;
    private final JobExecutionService jobExecutionService;

//    @PostConstruct
//    public void start(){
//        Thread worker = new Thread(() -> {
//
//            while (!Thread.currentThread().isInterrupted()) {
//
//                try {
//                    Long jobId = jobQueue.take();
//
//                    log.info("Worker received job {}", jobId);
//
//                    jobExecutionService.execute(jobId);
//
//                } catch (InterruptedException e) {
//
//                    Thread.currentThread().interrupt();
//                    log.info("Worker interrupted");
//
//                } catch (Exception e) {
//
//                    log.error("Error while executing job", e);
//                }
//            }
//
//        });
//
//        worker.setName("job-worker");
//        worker.start();
//
//    }


    @RabbitListener(queues = RabbitMQConfig.JOB_QUEUE,
            concurrency = "3"
    )
    public void consume(Long jobId) {

        log.info("Worker received job {} from RabbitMQ", jobId);

        boolean claimed = jobExecutionService.claimJob(jobId);

        jobExecutionService.executeSynchronously(jobId);
    }


}
