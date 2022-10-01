package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.model.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOpeningRepository extends JpaRepository< JobOpening,Integer> {

     List<JobOpening> findByJobOpeningId(String joId);


     List<JobOpening> findByUnEntity(String unEntity);
}
