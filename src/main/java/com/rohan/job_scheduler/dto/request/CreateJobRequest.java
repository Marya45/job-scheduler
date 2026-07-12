package com.rohan.job_scheduler.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Command is required")
    private String command;

    @NotNull(message = "Scheduling time is required")
    private LocalDateTime scheduledAt;

}
