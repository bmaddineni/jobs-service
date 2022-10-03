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
public class JsoupWFPService {

    Logger logger = LoggerFactory.getLogger(JsoupWFPService.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;

//            * "0 0 * * * *" = the top of every hour of every day.
//            * "*/10 * * * * *" = every ten seconds.
//            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//            * "0 0 0,6,12,18 * * *" = 12 am, 6 am, 12 pm and 6 pm of every day.
//            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//            * "0 0 0 25 12 ?" = every Christmas Day at midnight
    @Scheduled(cron = "0 0/1 * * * *")
    public void parseWFPCareers() throws IOException {


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

                            .build();

            if(jobOpeningRepository.findByJobOpeningId(wfpJoId).isEmpty()){
                jobOpeningRepository.save(jobOpening);

            }




        }



//        System.out.println( getAdditionalAttributesFromPostingPage("https://jobs.unicef.org/en-us/listing/?page=1&page-items=1000"));
        logger.info("WFP Jobs has been loaded!");

    }

   private HashMap<String , String > getAdditionalAttributesFromPostingPage(String url) throws IOException {
        Document wfpPostingPageDoc = SSLHelper.getConnection(url).get();

        HashMap<String, String> jobDetailsMap = new HashMap<>();
        Elements elements = wfpPostingPageDoc.select("div #search-results-content");
        for (int i = 0; i < elements.size(); i++) {
//
            Element element = elements.get(i);

            System.out.println(element.getElementsByTag("a").size());

//            System.out.println(element.select("div.row--teaser"));

//
//            Element keyElement = element.getElementsByClass("job-info-label").get(0);
//            Element valueElement = element.getElementsByClass("job-info-value").get(0);
//
//            jobDetailsMap.put(keyElement.text(),valueElement.text());
//
        }

        return jobDetailsMap;
    }



}
