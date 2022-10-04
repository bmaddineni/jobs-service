package com.oneun.jobsservice.service;

import com.oneun.jobsservice.model.JobOpeningLoadStatus;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobOpeningLoadStatusService {
    @Autowired
    private JobOpeningLoadStatusRepository loadStatusRepository;

    void save(JobOpeningLoadStatus loadStatus) {

        loadStatusRepository.save(loadStatus);

    }

    public Iterable findAll() {

       return loadStatusRepository.findAll();
    }
}
