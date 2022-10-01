package com.oneun.jobsservice.controller;

import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import com.oneun.jobsservice.service.JobOpeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobOpeningController {
    @Autowired
    JobOpeningRepository jobOpeningRepository;

    @Autowired
    JobOpeningService jobOpeningService;

    @GetMapping
    public Iterable findAll(){

        return jobOpeningService.findAll();
    }

    @GetMapping("/job/{joId}")
    public Iterable findByJobOpeningId(@PathVariable String joId){

        return jobOpeningService.findByJobOpeningId(joId);

    }

    @GetMapping("/entity/{unEntity}")
    public Iterable findByEntity(@PathVariable String unEntity){

        return jobOpeningService.findByUnEntity(unEntity);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobOpening create(@RequestBody JobOpening jobOpening) {
        return jobOpeningService.save(jobOpening);
    }
}
