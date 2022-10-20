package com.oneun.jobsservice.controller.elastic;

import com.oneun.jobsservice.dto.JobOpeningsByEntity;
import com.oneun.jobsservice.model.elastic.Customer;
import com.oneun.jobsservice.model.elastic.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs/es")
public class JobOpeningControllerEs {

    @Autowired
    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    @PostMapping("/save")
    public int saveCustomer(@RequestBody List<JobOpening> jobOpenings){

        jobOpeningElasticSearchRepository.saveAll(jobOpenings);

        return jobOpenings.size();
    }

    @GetMapping("/findAll")
    public Iterable<JobOpening> findAllCustomers() {

        return jobOpeningElasticSearchRepository.findAll();


    }

    @GetMapping("/findByEntity/{entity}")
    public List<JobOpening> findByFirstName(@PathVariable String entity){

        return jobOpeningElasticSearchRepository.findByUnEntity(entity);
    }



}
