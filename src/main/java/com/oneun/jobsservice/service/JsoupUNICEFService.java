package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.helper.SSLHelper;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
public class JsoupUNICEFService {

    Logger logger = LoggerFactory.getLogger(JsoupUNICEFService.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;

//            * "0 0 * * * *" = the top of every hour of every day.
//            * "*/10 * * * * *" = every ten seconds.
//            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//            * "0 0 0,6,12,18 * * *" = 12 am, 6 am, 12 pm and 6 pm of every day.
//            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//            * "0 0 0 25 12 ?" = every Christmas Day at midnight
//    @Scheduled(cron = "0 0/10 * * * *")
    public void parseUNICEFCareers() throws IOException,StringIndexOutOfBoundsException {

int counter = 0;
        Document unicefDoc = SSLHelper.getConnection(ApplicationConstants.UNICEF_Careers_URL).get();
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();

        //fetches all table rows
        Elements ele=unicefDoc.select("div #search-results-content").get(0).getElementsByClass("row-content--text-info");

        logger.info("UNICEF Jobs loading started!");

//        System.out.println(ele.get(0));

        for (int i = 0; i < ele.size(); i++) {

            Element tableElements = ele.get(i);

            String unicefJobPostingURLFull= tableElements.getElementsByAttribute("href").get(0).attr("href");

//            String unicefJobPostingURL= (unicefJobPostingURLString.length()>254) ? unicefJobPostingURLString.substring(0,254) : unicefJobPostingURLString;
                    String unicefJobId = Arrays.stream(Arrays.stream(unicefJobPostingURLFull
                    .split("/job/"))
                        .toArray()[1].toString()
                    .split("/"))
                        .toArray()[0].toString();
            String unicefJobPostingURL=ApplicationConstants.UNICEF_POSTING_LINK_URL_PREFIX+unicefJobId;
            String unicefPostingTitle = tableElements.getElementsByAttribute("href").get(0).text();

//            System.out.println(unicefJobId + " --->  " + unicefPostingTitle.length());
//            String unicefLevelFromPostingTitle = Arrays.stream(unicefPostingTitle.split(",")).toArray()[1].toString();

            String unicefDeadlineDate = tableElements.getElementsByTag("p").get(3).text();
            String unicefDutyStation = tableElements.getElementsByClass("location").get(0).text();
            String unicefBasicJobDescription = tableElements.getElementsByTag("p").get(1).text();

//            System.out.println(unicefBasicJobDescription);



//
            if(jobOpeningRepository.findByJobOpeningId(unicefJobId).isEmpty()){
                JobOpening jobOpening = JobOpening.builder()
                        .jobOpeningId(unicefJobId)
                        .postingUrl(unicefJobPostingURL)
                        .dutyStation(unicefDutyStation)
                        .deadlineDate(unicefDeadlineDate)
                        .jobTitle(unicefPostingTitle)
                        .addedDate(new Date())
                        .unEntity(ApplicationConstants.UNICEF)
                        .unicefJobDescrBasic(unicefBasicJobDescription)
                        .postingDescrRaw(getAdditionalAttributesFromPostingPage(unicefJobPostingURL))
                        .build();
                counter++;

                jobOpeningRepository.save(jobOpening);

            }




        }



//        System.out.println( getAdditionalAttributesFromPostingPage("https://jobs.unicef.org/en-us/listing/?page=1&page-items=1000"));
        logger.info(counter +" UNICEF Jobs has been loaded!");

    }

   private String getAdditionalAttributesFromPostingPage(String url) throws IOException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.select("#job-content").text();

    }



}
