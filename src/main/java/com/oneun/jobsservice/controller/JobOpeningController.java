package com.oneun.jobsservice.controller;

import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.service.JobOpeningLoadStatusService;
import com.oneun.jobsservice.service.JobOpeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobOpeningController {

    @Autowired
    private JobOpeningService jobOpeningService;

    @Autowired
    private JobOpeningLoadStatusService loadStatus;

    @CrossOrigin
    @GetMapping
    public Iterable findAll() {

        return jobOpeningService.findAll();
    }

    @CrossOrigin
    @GetMapping("/job/{joId}")
    public Iterable findByJobOpeningId(@PathVariable String joId) {

        return jobOpeningService.findByJobOpeningId(joId);

    }

    @CrossOrigin
    @GetMapping("/search")
    public Iterable keywordSearch(@RequestParam String keyword){

        return jobOpeningService.keywordSearch(keyword.toLowerCase());
    }

    @CrossOrigin
    @GetMapping("/entity/{unEntity}")
    public Iterable findByEntity(@PathVariable String unEntity) {

        return jobOpeningService.findByUnEntity(unEntity);

    }

    @CrossOrigin
    @GetMapping("/status")
    public Iterable getStatus() {

        return loadStatus.findAll();

    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobOpening create(@RequestBody JobOpening jobOpening) {
        return jobOpeningService.save(jobOpening);
    }

}
