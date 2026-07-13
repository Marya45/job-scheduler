package com.rohan.job_scheduler.service;

import com.rohan.job_scheduler.dto.request.CreateJobRequest;
import com.rohan.job_scheduler.dto.response.JobResponse;

import java.util.List;

public interface JobService {

    JobResponse createJob(CreateJobRequest job);

    List<JobResponse> getMyJobs();

    JobResponse getJobById(Long id);

    JobResponse updateJob(Long id,CreateJobRequest job);

    void deleteJob(Long id);
}
