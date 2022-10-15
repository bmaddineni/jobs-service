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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
public class JsoupWFPService {

    Logger logger = LoggerFactory.getLogger(JsoupWFPService.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;

    @Autowired
    private JobOpeningLoadStatusRepository loadStatusRepository;

    public void parseWFPCareers() throws IOException, SocketException  {
        Date startDate = new Date();


        int counter = 0;
        Document wfpDoc = SSLHelper.getConnection(ApplicationConstants.WFP_Careers_URL).get();
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();

        //fetches all table rows
        Elements ele=wfpDoc.getElementsByClass("careers--item");
        logger.info("WFP Jobs loading started!");


        for (int i = 0; i < ele.stream().count(); i++) {

            Element tableElements = ele.get(i);

            Elements rowTitle = tableElements.getElementsByTag("h2");
            Elements rowParams = tableElements.getElementsByClass("item--params");



            String wfpJobTitle = rowTitle.get(0).text();
            String wfpAreaOfExpertise = rowParams.get(0).child(1).text();
            String wfpCountry = rowParams.get(0).child(3).text();

            String wfpTypeOfContract = rowParams.get(0).child(5).text();

            String wfpClosingDate = rowParams.get(0).child(7).text();

            String wfpPostingUrl = rowParams.get(0).parent().nextElementSibling().child(0).attr("href");

            String wfpJoId = ApplicationConstants.WFP + "-" +Arrays.stream(wfpPostingUrl.split("career_job_req_id=")).toArray()[1].toString().trim();

            if (wfpJoId != null) {


                if (jobOpeningRepository.findByJobOpeningId(wfpJoId).isEmpty()) {
                    counter++;
                    JobOpening jobOpening = JobOpening.builder()
                            .jobOpeningId(wfpJoId)
                            .unEntity(ApplicationConstants.WFP)
                            .deadlineDate(wfpClosingDate)
                            .dutyStation(wfpCountry)
                            .jobFamily(wfpAreaOfExpertise)
                            .jobTitle(wfpJobTitle)
                            .postingUrl(wfpPostingUrl)
                            .wfpTypeOfContract(wfpTypeOfContract)
                            .addedDate(new Date())
                            .unicefJobDescrBasic(getAdditionalAttributesFromPostingPage(wfpPostingUrl))

                            .build();
                    jobOpeningRepository.save(jobOpening);

                }

            }

            JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                    .entity(ApplicationConstants.WFP)
                    .endDateTimestamp(new Date())
                    .startDateTimestamp(startDate)
                    .count(counter)
                    .build();

            loadStatusRepository.save(loadStatus);


        }



//        System.out.println( getAdditionalAttributesFromPostingPage("https://jobs.unicef.org/en-us/listing/?page=1&page-items=1000"));


        logger.info(counter + " WFP Jobs has been loaded!");

    }

    private String getAdditionalAttributesFromPostingPage(String url) throws IOException, SocketException {
        Document postingPageDoc = SSLHelper.getConnection(url).timeout(10000).get();

        return postingPageDoc.select("#jobAppPageTitle").text();

    }



}
