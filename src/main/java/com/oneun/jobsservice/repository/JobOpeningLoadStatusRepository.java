package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.model.JobOpeningLoadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobOpeningLoadStatusRepository extends JpaRepository<JobOpeningLoadStatus,Integer> {

}
