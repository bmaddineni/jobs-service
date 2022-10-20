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
public class JsoupILOService {

    Logger logger = LoggerFactory.getLogger(JsoupILOService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    public JsoupILOService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository, JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
        this.jobOpeningElasticSearchRepository = jobOpeningElasticSearchRepository;
    }

    public void parseILOCareers() throws IOException, SocketException  {
        Date startDate = new Date();


        int counter = 0;
        Document unescoDoc = SSLHelper.getConnection(ApplicationConstants.UNESCO_Careers_URL).get();

        String noOfEntries = Arrays.stream(unescoDoc.getElementsByClass("paginationLabel").get(0).text().split("of")).toArray()[1].toString();

        int noOfLoops = Integer.parseInt(noOfEntries.trim())/20;

        String unescoUrlFirstPart = Arrays.stream(ApplicationConstants.ILO_Careers_URL.split("2842101")).toArray()[0].toString() + "2842101/";

        String unescoUrlSecondPart = Arrays.stream(ApplicationConstants.ILO_Careers_URL.split("2842101")).toArray()[1].toString();

        int loop = 0;
        logger.info("ILO Jobs loading started!");


        for (int j = 0; j <= noOfLoops; j++) {

            Document unescoDocPage = SSLHelper.getConnection(unescoUrlFirstPart + loop + unescoUrlSecondPart).get();

//            System.out.println(unescoUrlFirstPart + loop + unescoUrlSecondPart);

            loop = loop + 20;
//            System.out.println(loop+25);


            Elements ele = unescoDocPage.getElementsByClass("data-row");


            for (int i = 0; i < (long) ele.size(); i++) {

                Element tableElements = ele.get(i);
//            System.out.println(tableElements);

                String iloJobTitle = tableElements.getElementsByClass("jobTitle-link").get(0).text();
                String shortUrl = tableElements.getElementsByClass("jobTitle-link").get(0).attr("href");
                String iloJobURL = ApplicationConstants.ILO_POSTING_LINK_URL_PREFIX + shortUrl;

                String iloDutyStation = tableElements.getElementsByClass("jobDepartment").get(0).text();

                String iloJobFacility = tableElements.getElementsByClass("jobShifttype").get(0).text();
                String iloJobId = ApplicationConstants.ILO+"-"+tableElements.getElementsByClass("jobFacility").get(0).text();


//            System.out.println(tableElements);

                if (iloJobId != null) {

                    if (jobOpeningRepository.findByJobOpeningId(iloJobId).isEmpty()) {
                        counter++;
                        JobOpening jobOpening = JobOpening.builder()
                                .jobOpeningId(iloJobId)
                                .unEntity(ApplicationConstants.ILO)
//                                .deadlineDate(unescoDeadlineDate)
                                .dutyStation(iloDutyStation)
                                .jobFamily(iloJobFacility)
                                .jobTitle(iloJobTitle)
                                .postingUrl(iloJobURL)
//                                .level(unescoGradeLevel)
                                .addedDate(new Date())
                                .postingDescrRaw(getAdditionalAttributesFromPostingPage(iloJobURL))

                                .build();
                        jobOpeningRepository.save(jobOpening);
                    }
                    if (jobOpeningElasticSearchRepository.findByJobOpeningId(iloJobId).isEmpty()) {
                        com.oneun.jobsservice.model.elastic.JobOpening jobOpeningEs = com.oneun.jobsservice.model.elastic.JobOpening.builder()
                                .id(iloJobId)
                                .jobOpeningId(iloJobId)
                                .unEntity(ApplicationConstants.ILO)
//                                .deadlineDate(unescoDeadlineDate)
                                .dutyStation(iloDutyStation)
                                .jobFamily(iloJobFacility)
                                .jobTitle(iloJobTitle)
                                .postingUrl(iloJobURL)
//                                .level(unescoGradeLevel)
                                .addedDate(new Date())
                                .postingDescrRaw(getAdditionalAttributesFromPostingPage(iloJobURL))

                                .build();
                        jobOpeningElasticSearchRepository.save(jobOpeningEs);

                    }
                }

            }


//        System.out.println( getAdditionalAttributesFromPostingPage("https://jobs.unicef.org/en-us/listing/?page=1&page-items=1000"));



        }
        JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                .entity(ApplicationConstants.ILO)
                .endDateTimestamp(new Date())
                .startDateTimestamp(startDate)
                .count(counter)
                .build();

        loadStatusRepository.save(loadStatus);
        logger.info(counter + " ILO Jobs has been loaded!");

    }

    private String getAdditionalAttributesFromPostingPage(String url) throws IOException , SocketException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.getElementsByClass("jobdescription").text();

    }


}
