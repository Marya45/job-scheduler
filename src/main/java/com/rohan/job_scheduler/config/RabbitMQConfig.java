package com.rohan.job_scheduler.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String JOB_QUEUE = "job.queue";
    public static final String JOB_EXCHANGE = "job.exchange";
    public static final String JOB_ROUTING_KEY = "job.execute";

    public static final String JOB_DLX = "job.dlx";
    public static final String JOB_DLQ = "job.dlq";
    public static final String JOB_DLQ_ROUTING_KEY = "job.failed";

    @Bean
    public Queue jobQueue() {
        return new Queue(
                JOB_QUEUE,
                true,
                false,
                false,
                Map.of(
                        "x-dead-letter-exchange", JOB_DLX,
                        "x-dead-letter-routing-key", JOB_DLQ_ROUTING_KEY
                )
        );
    }

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(JOB_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(JOB_DLX, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(JOB_DLQ)
                .build();
    }

    @Bean
    public Binding jobBinding(
            Queue jobQueue,
            DirectExchange jobExchange
    ) {
        return BindingBuilder
                .bind(jobQueue)
                .to(jobExchange)
                .with(JOB_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(
            Queue deadLetterQueue,
            DirectExchange deadLetterExchange
    ) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(JOB_DLQ_ROUTING_KEY);
    }

}
