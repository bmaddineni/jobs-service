package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.helper.SSLHelper;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.model.JobOpeningLoadStatus;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
public class JsoupUNICEFService {

    Logger logger = LoggerFactory.getLogger(JsoupUNICEFService.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;

    @Autowired
    private JobOpeningLoadStatusRepository loadStatusRepository;

    public void parseUNICEFCareers() throws IOException,StringIndexOutOfBoundsException,  SocketException  {

        int counter = 0;
        Date startDate = new Date();

        Document unicefDoc = SSLHelper.getConnection(ApplicationConstants.UNICEF_Careers_URL).get();
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();

        //fetches all table rows
        Elements ele=unicefDoc.select("div #search-results-content").get(0).getElementsByClass("row-content--text-info");

        logger.info("UNICEF Jobs loading started!");

//        System.out.println(ele.get(0));

        for (int i = 0; i < ele.size(); i++) {

            Element tableElements = ele.get(i);

            String unicefJobPostingURLFull= tableElements.getElementsByAttribute("href").get(0).attr("href");

                    String unicefJobId = ApplicationConstants.UNICEF+"-"+Arrays.stream(Arrays.stream(unicefJobPostingURLFull
                    .split("/job/"))
                        .toArray()[1].toString()
                    .split("/"))
                        .toArray()[0].toString();
            String unicefJobPostingURL=ApplicationConstants.UNICEF_POSTING_LINK_URL_PREFIX+unicefJobId;
            String unicefPostingTitle = tableElements.getElementsByAttribute("href").get(0).text();


            String unicefDeadlineDate = tableElements.getElementsByTag("p").get(3).text().replace("Deadline: ","");
            String unicefDutyStation = tableElements.getElementsByClass("location").get(0).text();
            String unicefBasicJobDescription = tableElements.getElementsByTag("p").get(1).text();

            if (unicefJobId != null) {


            if(unicefJobId != null && jobOpeningRepository.findByJobOpeningId(unicefJobId).isEmpty()) {
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




        }



        JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                .entity(ApplicationConstants.UNICEF)
                .endDateTimestamp(new Date())
                .startDateTimestamp(startDate)
                .count(counter)
                .build();

        loadStatusRepository.save(loadStatus);

        logger.info(counter +" UNICEF Jobs has been loaded!");


    }

   private String getAdditionalAttributesFromPostingPage(String url) throws IOException, SocketException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.select("#job-content").text();

    }



}
