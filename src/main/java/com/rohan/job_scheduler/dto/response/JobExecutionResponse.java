package com.rohan.job_scheduler.dto.response;

import com.rohan.job_scheduler.entity.JobStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobExecutionResponse {

    private Long id;

    private JobStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer exitCode;

    private String output;

    private String errorMessage;

}
