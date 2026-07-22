package com.rohan.job_scheduler.repository;

import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution,Long> {

    List<JobExecution> findByJobOrderByStartedAtDesc(Job job);

}
