package com.rohan.job_scheduler.queue;

import com.rohan.job_scheduler.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQJobQueue implements JobQueue {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void submit(Long jobId) {
        log.info("Publishing job {} to RabbitMQ", jobId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.JOB_EXCHANGE,
                RabbitMQConfig.JOB_ROUTING_KEY,
                jobId
        );
    }
}
