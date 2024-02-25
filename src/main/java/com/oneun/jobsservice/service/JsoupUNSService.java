package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.helper.SSLHelper;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.model.JobOpeningLoadStatus;
import com.oneun.jobsservice.model.LoadStatus;
import com.oneun.jobsservice.repository.JobOpeningElasticSearchRepository;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.jsoup.nodes.Document;
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
public class JsoupUNSService {

    Logger logger = LoggerFactory.getLogger(JsoupUNSService.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;
    @Autowired
    private JobOpeningLoadStatusRepository loadStatusRepository;

    @Autowired
    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    public JsoupUNSService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository, JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
        this.jobOpeningElasticSearchRepository = jobOpeningElasticSearchRepository;
    }

    //            * "0 0 * * * *" = the top of every hour of every day.
//            * "*/10 * * * * *" = every ten seconds.
//            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//            * "0 0 0,6,12,18 * * *" = 12 am, 6 am, 12 pm and 6 pm of every day.
//            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//            * "0 0 0 25 12 ?" = every Christmas Day at midnight
//    @Scheduled(cron = "0 0/30 * * * *")
    public void parseUNCareers() throws IOException, SocketException {


        Date startDate = new Date();

        int counter = 0;
        Document unsDoc = SSLHelper.getConnection(ApplicationConstants.UNS1_Careers_URL).get();
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();

        //fetches all table rows
        Elements ele=unsDoc.getElementsByTag("tr");

        logger.info("UNS Jobs has been started!");


        for (int i = 0; i < ele.stream().count(); i++) {


            if(i % 2 ==1){
                if(ele.stream().count() !=1){
                    String jo = ApplicationConstants.UNS+"-"+Arrays.stream(ele.get(i)
                                    .getElementsByTag("td").get(0)
                            .getElementsByTag("a")
                            .attr("href")
                            .split("="))
                            .toArray()[1].toString();
                    String joHeaderDetails = ele.get(i+1).getElementsByTag("td").html();
                    String joTitle = ele.get(i).getElementsByTag("td").get(0)
                            .getElementsByTag("a").text();
                    String postingUrl = ele.get(i).getElementsByTag("td").get(0)
                            .getElementsByTag("a").attr("href");

                    HashMap<String,String> newHashMap = getJobHeaderDetailsByString(joHeaderDetails.trim());
                    newHashMap.put(ApplicationConstants.UNS_FIELD_JOB_TITLE_KEY,joTitle);
                    newHashMap.put(ApplicationConstants.UNS_FIELD_POSTING_URL_KEY,postingUrl);
                    hashMap.put(jo.trim(),newHashMap);
                    HashMap<String, String> jobHeader = hashMap.get(jo);


//                    System.out.println(jobOpeningRepository.findByJobOpeningId(jo).isEmpty());
                    if (jo != null) {

                        if (jobOpeningRepository.findByJobOpeningId(jo).isEmpty()) {
                            counter++;

                            JobOpening jobOpening = JobOpening.builder()
                                    .jobOpeningId(jo)
                                    .jobTitle(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_TITLE_KEY).toString())
                                    .unEntity(
                                            jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY).contains(ApplicationConstants.UNRWA) ? ApplicationConstants.UNRWA
                                                    : jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY).contains(ApplicationConstants.ICAO_DESCR) ? ApplicationConstants.ICAO
                                                    : ApplicationConstants.UNS
                                    )
                                    .deadlineDate(jobHeader.get(ApplicationConstants.UNS_FIELD_DEADLINE_DATE_KEY).toString())
                                    .postingUrl(ApplicationConstants.UNS_POSTING_LINK_URL_PREFIX + jobHeader.get(ApplicationConstants.UNS_FIELD_POSTING_URL_KEY))
                                    .level(jobHeader.get(ApplicationConstants.UNS_FIELD_LEVEL_KEY))
                                    .departmentOffice(jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY))
                                    .dutyStation(jobHeader.get(ApplicationConstants.UNS_FIELD_DUTY_STATION_KEY))
                                    .jobFamily(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_FAMILY_KEY))
                                    .jobNetwork(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_NETWORK_KEY))
                                    .postedDate(jobHeader.get(ApplicationConstants.UNS_FIELD_POSTED_DATE_KEY))
                                    .postingDescrRaw(getAdditionalAttributesFromPostingPage(ApplicationConstants.UNS_POSTING_LINK_URL_PREFIX + jobHeader.get(ApplicationConstants.UNS_FIELD_POSTING_URL_KEY)))
                                    .addedDate(new Date())
                                    .build();


                            jobOpeningRepository.save(jobOpening);

                        }



                        if (jobOpeningElasticSearchRepository.findByJobOpeningId(jo).isEmpty()) {

                            com.oneun.jobsservice.model.elastic.JobOpening jobOpeningES = com.oneun.jobsservice.model.elastic.JobOpening.builder()
                                    .id(jo)
                                    .jobOpeningId(jo)
                                    .jobTitle(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_TITLE_KEY).toString())
                                    .unEntity(
                                            jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY).contains(ApplicationConstants.UNRWA) ? ApplicationConstants.UNRWA
                                                    : jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY).contains(ApplicationConstants.ICAO_DESCR) ? ApplicationConstants.ICAO
                                                    : ApplicationConstants.UNS
                                    )
                                    .deadlineDate(jobHeader.get(ApplicationConstants.UNS_FIELD_DEADLINE_DATE_KEY).toString())
                                    .postingUrl(ApplicationConstants.UNS_POSTING_LINK_URL_PREFIX + jobHeader.get(ApplicationConstants.UNS_FIELD_POSTING_URL_KEY))
                                    .level(jobHeader.get(ApplicationConstants.UNS_FIELD_LEVEL_KEY))
                                    .departmentOffice(jobHeader.get(ApplicationConstants.UNS_FIELD_DEPT_OFFICE_KEY))
                                    .dutyStation(jobHeader.get(ApplicationConstants.UNS_FIELD_DUTY_STATION_KEY))
                                    .jobFamily(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_FAMILY_KEY))
                                    .jobNetwork(jobHeader.get(ApplicationConstants.UNS_FIELD_JOB_NETWORK_KEY))
                                    .postedDate(jobHeader.get(ApplicationConstants.UNS_FIELD_POSTED_DATE_KEY))
                                    .postingDescrRaw(getAdditionalAttributesFromPostingPage(ApplicationConstants.UNS_POSTING_LINK_URL_PREFIX + jobHeader.get(ApplicationConstants.UNS_FIELD_POSTING_URL_KEY)))
                                    .addedDate(new Date())
                                    .build();


                            jobOpeningElasticSearchRepository.save(jobOpeningES);

                        }

                    }
                }
            }


            JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                    .entity(ApplicationConstants.UNS)
                    .endDateTimestamp(new Date())
                    .startDateTimestamp(startDate)
                    .count(counter)
                    .loadStatus(LoadStatus.IN_PROGRESS)
                    .build();

            loadStatusRepository.save(loadStatus);

        }
        logger.info(counter +" UNS Jobs has been loaded!");



        if (!loadStatusRepository.findByEntity(ApplicationConstants.UNS).isEmpty())
        {
            JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
//                    .id(loadStatusRepository.findByEntity(ApplicationConstants.UNS).get(0).getId())
                    .entity(ApplicationConstants.UNS)
                    .endDateTimestamp(new Date())
                    .startDateTimestamp(startDate)
                    .count(counter)
                    .loadStatus(LoadStatus.IN_PROGRESS)
                    .build();
            loadStatusRepository.save(loadStatus);

        }


    }

    private HashMap<String,String> getJobHeaderDetailsByString(String jobHeaderDetailsString) {

        HashMap<String, String> hashMap = new HashMap<>();

        for (int i = 0; i < Arrays.stream(jobHeaderDetailsString.split("<br>")).toArray().length; i++) {

            String key = Arrays.stream(jobHeaderDetailsString.split("<br>")).toArray()[i].toString();
            hashMap.put(Arrays.stream(key.split(":")).toArray()[0].toString().trim(), Arrays.stream(key.split(":")).toArray()[1].toString().trim());
        }

        return hashMap;
    }



    private String getAdditionalAttributesFromPostingPage(String url) throws IOException, SocketException  {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.select("#jd_content").text();

    }
}
