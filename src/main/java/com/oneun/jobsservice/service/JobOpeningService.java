package com.oneun.jobsservice.service;

import com.oneun.jobsservice.dto.JobOpeningsByEntity;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobOpeningService {
    @Autowired
    private JobOpeningRepository jobOpeningRepository;


    public Iterable findAll() {

       return jobOpeningRepository.findAll();
    }

    public Iterable findByJobOpeningId(String joId) {

       return jobOpeningRepository.findByJobOpeningId(joId);
    }

    public Page<JobOpening> findByUnEntity(String unEntity, int page, int size) {

       return jobOpeningRepository.findByUnEntity(unEntity,PageRequest.of(page,size));
    }


    public JobOpening save(JobOpening jobOpening) {

      return   jobOpeningRepository.save(jobOpening);
    }


    public List<JobOpening> keywordSearch(String keyword) {

        return jobOpeningRepository.search(keyword);
    }

    public Page<JobOpening> findAllWithPagination(int page, int size)
    {
        return jobOpeningRepository.findAll(PageRequest.of(page,size));
    }

    public List<JobOpeningsByEntity> findJobsCountByEntity() {

        return jobOpeningRepository.countTotalJobsByEntity();
    }

    public void deleteByEntity(String entity) {

      List<JobOpening> jobs = jobOpeningRepository.findByUnEntity(entity);
        System.out.println(entity);
        System.out.println(jobs.size());

     jobOpeningRepository.deleteAllInBatch(jobs);

    }
}
