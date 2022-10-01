package com.oneun.jobsservice.service;

import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import static org.jsoup.Jsoup.*;

@Service
public class JsoupUNSService {

    @Autowired
    JobOpeningRepository jobOpeningRepository;
    public JobOpening parseUNCareers() throws IOException {

        Document doc = connect("http://www.javatpoint.com").get();
        String title = doc.title();
        System.out.println("title is: " + title);
        JobOpening jobOpening = new JobOpening();
        jobOpening.setJobTitle(UUID.randomUUID().toString());
        jobOpening.setJobOpeningId(UUID.randomUUID().toString());
        jobOpening.setUnEntity("UNS");
        jobOpening.setMoreInfo(UUID.randomUUID().toString());

        return jobOpeningRepository.save(jobOpening);
    }


}
