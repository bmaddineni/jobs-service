package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.dto.JobOpeningsByEntity;
import com.oneun.jobsservice.model.JobOpening;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.*;
import java.util.List;

public interface JobOpeningRepository extends JpaRepository< JobOpening,Integer> {

     List<JobOpening> findByJobOpeningId(String joId);

//     Page<List<JobOpening>> findByJobTitleContains(String title, Pageable pageable);


    List<JobOpening> findByUnEntity(String unEntity);

    Page<JobOpening> findByUnEntity(String unEntity, Pageable pageable);

     @Query("SELECT j FROM JOB_OPENING_TBL j WHERE LOWER(CONCAT( j.unEntity, ' ', j.jobTitle, ' ', j.dutyStation, ' ', j.jobOpeningId )) LIKE %?1%")
    List<JobOpening> search(String keyword);


     @Query("SELECT new com.oneun.jobsservice.dto.JobOpeningsByEntity(j.unEntity, COUNT(j.unEntity)) FROM com.oneun.jobsservice.model.JobOpening AS j GROUP BY j.unEntity")
     List<JobOpeningsByEntity> countTotalJobsByEntity();
}
