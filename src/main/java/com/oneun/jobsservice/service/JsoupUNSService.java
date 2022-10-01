package com.oneun.jobsservice.service;

import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.jsoup.Jsoup.*;

@Service
public class JsoupUNSService {

    @Autowired
    JobOpeningRepository jobOpeningRepository;

//            * "0 0 * * * *" = the top of every hour of every day.
//            * "*/10 * * * * *" = every ten seconds.
//            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//            * "0 0 0,6,12,18 * * *" = 12 am, 6 am, 12 pm and 6 pm of every day.
//            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//            * "0 0 0 25 12 ?" = every Christmas Day at midnight
    @Scheduled(cron = "0 0/1 * * * *")
    public JobOpening parseUNCareers() throws IOException {

        Document doc = connect("http://www.javatpoint.com").get();
        String title = doc.title();
        System.out.println("title is: " + title +""+new Date());
        JobOpening jobOpening = new JobOpening();
        jobOpening.setJobTitle(UUID.randomUUID().toString());
        jobOpening.setJobOpeningId(UUID.randomUUID().toString());
        jobOpening.setUnEntity("UNS");
        jobOpening.setMoreInfo(UUID.randomUUID().toString());

        return jobOpeningRepository.save(jobOpening);
    }


}
