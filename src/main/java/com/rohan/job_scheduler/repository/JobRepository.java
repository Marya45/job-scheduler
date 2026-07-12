package com.rohan.job_scheduler.repository;

import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    List<Job> findByCreatedBy(User user);

}
