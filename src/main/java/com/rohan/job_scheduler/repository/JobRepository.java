package com.rohan.job_scheduler.repository;

import com.rohan.job_scheduler.entity.Job;
import com.rohan.job_scheduler.entity.JobStatus;
import com.rohan.job_scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {

    List<Job> findByCreatedBy(User user);

    Optional<Job> findByIdAndCreatedBy(Long id, User createdBy);

    List<Job> findByStatusAndScheduledAtLessThanEqual(JobStatus status, LocalDateTime scheduledAt);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Job j
    SET j.status = :newStatus
    WHERE j.id = :jobId
      AND j.status = :expectedStatus
    """)
    int updateStatusIfCurrentStatus(
            @Param("jobId") Long jobId,
            @Param("expectedStatus") JobStatus expectedStatus,
            @Param("newStatus") JobStatus newStatus
    );

}
