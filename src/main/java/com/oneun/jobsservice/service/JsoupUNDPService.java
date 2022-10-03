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
public class JsoupUNDPService {

    Logger logger = LoggerFactory.getLogger(JsoupUNDPService.class);
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
    public void parseUNDPCareers() throws IOException {


        Document unsDoc = SSLHelper.getConnection(ApplicationConstants.UNDP_Careers_URL).get();
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();

        //fetches all table rows
        Elements ele=unsDoc.getElementsByClass("table-sortable");
        logger.info("UNDP Jobs loading started!");


        for (int i = 0; i < ele.stream().count(); i++) {

            Element tableElements = ele.get(i);

            Elements rowElements = tableElements.getElementsByTag("tr");


            for (int j = 0; j < rowElements.stream().count(); j++) {
                HashMap<String,HashMap<String,String>> hashMap1 = new HashMap<>();
                HashMap<String,String> hashMap2 = new HashMap<>();

                Element rowElement = rowElements.get(j);

                if(rowElement.hasAttr("class"))
                {

                    Elements rowCells = rowElement.getElementsByTag("td");
                    String postingTitle = rowCells.get(0).text();
                    String undpJobId = postingTitle.contains("**") ?
                            ApplicationConstants.UNDP+"-"+Arrays.stream(rowCells.get(0)
                                            .getElementsByTag("a")
                                            .get(0).attr("href")
                                            .split("/job/"))
                                    .toArray()[1].toString()

                            : ApplicationConstants.UNDP+"-"+Arrays.stream(rowCells.get(0)
                                    .getElementsByTag("a")
                                    .get(0).attr("href")
                                    .split("="))
                            .toArray()[1].toString();

                    String postingURLShort = rowCells.get(0).getElementsByTag("a").get(0).attr("href");
                    String postingURL = postingURLShort.contains(ApplicationConstants.UNDP_ORACLE_HCM_IDENTIFIER)? postingURLShort : ApplicationConstants.UNDP_POSTING_LINK_URL_PREFIX +postingURLShort;
                    String undpJobType = rowCells.get(1).text();
                    String undpjobLevel = rowCells.get(2).text();
                    String undpDeadline = rowCells.get(3).text();
                    String undpDutyStation = rowCells.get(4).text();

                    JobOpening jobOpening = JobOpening.builder()
                            .jobOpeningId(undpJobId)
                            .unEntity(ApplicationConstants.UNDP)
                            .level(undpjobLevel)
                            .postingUrl(postingURL)
                            .deadlineDate(undpDeadline)
                            .dutyStation(undpDutyStation)
                            .jobTitle(postingTitle.trim())
                            .addedDate(new Date())
                            .build();
                    if (jobOpeningRepository.findByJobOpeningId(undpJobId).isEmpty()) {

                        jobOpeningRepository.save(jobOpening);


                    }


                }

            }

            }


        logger.info("UNDP Jobs has been loaded!");

    }

   private HashMap<String , String > getAdditionalAttributesFromPostingPage(String url) throws IOException {
        Document undpPostingPageDoc = SSLHelper.getConnection(url).get();

        HashMap<String,String> jobDetailsMap = new HashMap<>();
        Elements elements = undpPostingPageDoc.getElementsByClass("careers--item");
       System.out.println(elements);
//        for (int i = 0; i < elements.size(); i++) {
//
//            Element element = elements.get(i);
//
//            Element keyElement = element.getElementsByClass("job-info-label").get(0);
//            Element valueElement = element.getElementsByClass("job-info-value").get(0);
//
//            jobDetailsMap.put(keyElement.text(),valueElement.text());
//
//        }

        return jobDetailsMap;
    }



}
