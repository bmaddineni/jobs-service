package com.oneun.jobsservice.controller;

import com.oneun.jobsservice.dto.JobOpeningsByEntity;
import com.oneun.jobsservice.helper.LinkedInApi;
import com.oneun.jobsservice.helper.TwitterApi;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.service.JobOpeningLoadStatusService;
import com.oneun.jobsservice.service.JobOpeningService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import twitter4j.TwitterException;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobOpeningController {

    @Autowired
    private JobOpeningService jobOpeningService;

    @Autowired
    private JobOpeningLoadStatusService loadStatus;

    @Autowired
    private LinkedInApi linkedInApi;

    @Autowired
    private TwitterApi twitterApi;

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
    public List<JobOpening> keywordSearch(@RequestParam String keyword){

        return jobOpeningService.keywordSearch(keyword.toLowerCase());
    }

    @CrossOrigin
    @GetMapping("/entity/{unEntity}")
    public Iterable findByEntity(@PathVariable String unEntity,@RequestParam int page, @RequestParam int size) {

        return jobOpeningService.findByUnEntity(unEntity,page,size);

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

    @GetMapping("/paged")
    public Page<JobOpening> findByTitle(@RequestParam int page, @RequestParam int size){

        return jobOpeningService.findAllWithPagination(page,size);
    }
    @PostMapping("/linkedin/post")
    public String submitPost(@RequestBody Object text) throws JSONException {

        String message = new JSONObject(text.toString()).getString("message");
        System.out.println(message);

       JSONObject response= linkedInApi.submitLinkedInPost(message);

        return "https://www.linkedin.com/feed/update/"+response.getString("id");


    }

    @PostMapping("/twitter/tweet")
    public String submitTweet(@RequestBody Object text) throws JSONException, TwitterException {

        String message = new JSONObject(text.toString()).getString("message");
        System.out.println(message);
        twitterApi.createTweet(message);



        return twitterApi.createTweet(message);


    }
    @GetMapping("/countByEntity")
    public List<JobOpeningsByEntity> findJobsCountByEntity(){

       return jobOpeningService.findJobsCountByEntity();



    }

    @PostMapping("/delete/entity/{entity}")
    public void deleteJobsByEntity(String entity){

        jobOpeningService.deleteByEntity(entity);



    }
}
