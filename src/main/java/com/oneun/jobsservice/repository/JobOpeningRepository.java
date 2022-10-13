package com.oneun.jobsservice.repository;

import com.oneun.jobsservice.model.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobOpeningRepository extends JpaRepository< JobOpening,Integer> {

     List<JobOpening> findByJobOpeningId(String joId);


     List<JobOpening> findByUnEntity(String unEntity);

//     @Query("SELECT j FROM JOB_OPENING_TBL j WHERE LOWER(CONCAT( j.unEntity, ' ', j.jobTitle, ' ', j.dutyStation, ' ' , j.postingDescrRaw, ' ', j.jobFamily, ' ', j.jobNetwork, ' ', j.level, ' ', j.jobOpeningId, ' ',j.departmentOffice )) LIKE %?1%")
@Query("SELECT j FROM JOB_OPENING_TBL j WHERE LOWER(CONCAT( j.unEntity, ' ', j.jobTitle, ' ', j.dutyStation, ' ', j.jobOpeningId )) LIKE %?1%")
 List<JobOpening> search(String keyword);
}
