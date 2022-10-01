package com.oneun.jobsservice.service;

import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobOpeningService {
    @Autowired
    JobOpeningRepository jobOpeningRepository;


    public Iterable findAll() {

       return jobOpeningRepository.findAll();
    }

    public Iterable findByJobOpeningId(String joId) {

       return jobOpeningRepository.findByJobOpeningId(joId);
    }

    public Iterable findByUnEntity(String unEntity) {

       return jobOpeningRepository.findByUnEntity(unEntity);
    }


    public JobOpening save(JobOpening jobOpening) {

      return   jobOpeningRepository.save(jobOpening);
    }
}
