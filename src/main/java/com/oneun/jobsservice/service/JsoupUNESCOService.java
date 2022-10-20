package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.helper.SSLHelper;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.model.JobOpeningLoadStatus;
import com.oneun.jobsservice.repository.JobOpeningElasticSearchRepository;
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

@Service
public class JsoupUNESCOService {

    Logger logger = LoggerFactory.getLogger(JsoupUNESCOService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    @Autowired
    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    public JsoupUNESCOService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
    }

    public void parseUNESCOCareers() throws IOException, SocketException  {
        Date startDate = new Date();


        int counter = 0;
        Document unescoDoc = SSLHelper.getConnection(ApplicationConstants.UNESCO_Careers_URL).get();

        String noOfEntries = Arrays.stream(unescoDoc.getElementsByClass("paginationLabel").get(0).text().split("of")).toArray()[1].toString();

        int noOfLoops = Integer.parseInt(noOfEntries.trim())/25;

        String unescoUrlFirstPart = Arrays.stream(ApplicationConstants.UNESCO_Careers_URL.split("782502")).toArray()[0].toString() + "782502/";

        String unescoUrlSecondPart = Arrays.stream(ApplicationConstants.UNESCO_Careers_URL.split("782502")).toArray()[1].toString();

        int loop = 0;
        logger.info("UNESCO Jobs loading started!");


        for (int j = 0; j <= noOfLoops; j++) {

            Document unescoDocPage = SSLHelper.getConnection(unescoUrlFirstPart + loop + unescoUrlSecondPart).get();

//            System.out.println(unescoUrlFirstPart + loop + unescoUrlSecondPart);

            loop = loop + 25;
//            System.out.println(loop+25);


            Elements ele = unescoDocPage.getElementsByClass("data-row");


            for (int i = 0; i < (long) ele.size(); i++) {

                Element tableElements = ele.get(i);
//            System.out.println(tableElements);

                String unescoJobTitle = tableElements.getElementsByClass("jobTitle-link").get(0).text();
                String shortUrl = tableElements.getElementsByClass("jobTitle-link").get(0).attr("href");
                String unescoJobURL = ApplicationConstants.UNESCO_POSTING_LINK_URL_PREFIX + shortUrl;

                String unescoJobId = Arrays.stream(shortUrl.split("/")).toArray()[3].toString();
                String unescoDutyStation = tableElements.getElementsByClass("jobLocation").get(0).text();

                String unescoJobFacility = tableElements.getElementsByClass("jobFacility").get(0).text();
                String unescoGradeLevel = tableElements.getElementsByClass("jobDepartment").get(0).text();

                String unescoDeadlineDate = tableElements.getElementsByClass("jobShifttype").get(0).text();

//            System.out.println(tableElements);

                if (unescoJobId != null) {

                    if (jobOpeningRepository.findByJobOpeningId(unescoJobId).isEmpty()) {
                        counter++;
                        JobOpening jobOpening = JobOpening.builder()
                                .jobOpeningId(unescoJobId)
                                .unEntity(ApplicationConstants.UNESCO)
                                .deadlineDate(unescoDeadlineDate)
                                .dutyStation(unescoDutyStation)
                                .jobFamily(unescoJobFacility)
                                .jobTitle(unescoJobTitle)
                                .postingUrl(unescoJobURL)
                                .level(unescoGradeLevel)
                                .addedDate(new Date())
                                .postingDescrRaw(getAdditionalAttributesFromPostingPage(unescoJobURL))

                                .build();
                        jobOpeningRepository.save(jobOpening);

                    }

                    if (jobOpeningElasticSearchRepository.findByJobOpeningId(unescoJobId).isEmpty()) {
                        com.oneun.jobsservice.model.elastic.JobOpening jobOpeningES = com.oneun.jobsservice.model.elastic.JobOpening.builder()
                                .id(unescoJobId)
                                .jobOpeningId(unescoJobId)
                                .unEntity(ApplicationConstants.UNESCO)
                                .deadlineDate(unescoDeadlineDate)
                                .dutyStation(unescoDutyStation)
                                .jobFamily(unescoJobFacility)
                                .jobTitle(unescoJobTitle)
                                .postingUrl(unescoJobURL)
                                .level(unescoGradeLevel)
                                .addedDate(new Date())
                                .postingDescrRaw(getAdditionalAttributesFromPostingPage(unescoJobURL))

                                .build();
                        jobOpeningElasticSearchRepository.save(jobOpeningES);

                    }
                }

            }


//        System.out.println( getAdditionalAttributesFromPostingPage("https://jobs.unicef.org/en-us/listing/?page=1&page-items=1000"));



        }
        JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                .entity(ApplicationConstants.UNESCO)
                .endDateTimestamp(new Date())
                .startDateTimestamp(startDate)
                .count(counter)
                .build();

        loadStatusRepository.save(loadStatus);
        logger.info(counter + " UNESCO Jobs has been loaded!");

    }

    private String getAdditionalAttributesFromPostingPage(String url) throws IOException , SocketException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.getElementsByClass("jobdescription").text();

    }


}
