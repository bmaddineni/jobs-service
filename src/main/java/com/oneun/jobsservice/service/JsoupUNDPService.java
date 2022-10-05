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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

@Service
public class JsoupUNDPService {

    Logger logger = LoggerFactory.getLogger(JsoupUNDPService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    public JsoupUNDPService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
    }

    //            * "0 0 * * * *" = the top of every hour of every day.
//            * "*/10 * * * * *" = every ten seconds.
//            * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//            * "0 0 0,6,12,18 * * *" = 12 am, 6 am, 12 pm and 6 pm of every day.
//            * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//            * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//            * "0 0 0 25 12 ?" = every Christmas Day at midnight
//    @Scheduled(cron = "0 0/1 * * * *")
    public void parseUNDPCareers() throws IOException {

        Date startDate = new Date();

        Document unsDoc = SSLHelper.getConnection(ApplicationConstants.UNDP_Careers_URL).get();

        //fetches all table rows
        Elements ele = unsDoc.getElementsByClass("table-sortable");
        logger.info("UNDP Jobs loading started!");
        int counter = 0;

        double i_max = (long) ele.size();
        for (int i = 0; i < (long) ele.size(); i++) {

            Element tableElements = ele.get(i);

            Elements rowElements = tableElements.getElementsByTag("tr");
            double j_double = (long) rowElements.size();

            for (int j = 0; j < (long) rowElements.size(); j++) {

                Element rowElement = rowElements.get(j);

                if (rowElement.hasAttr("class")) {

                    Elements rowCells = rowElement.getElementsByTag("td");
                    String postingTitle = rowCells.get(0).text();

                    String postingURLShort = rowCells.get(0).getElementsByTag("a").get(0).attr("href");


                    String undpJobId = postingURLShort.contains(ApplicationConstants.UNDP_ORACLE_HCM_IDENTIFIER) ?
                            ApplicationConstants.UNDP + "-" + Arrays.stream(rowCells.get(0)
                                            .getElementsByTag("a")
                                            .get(0).attr("href")
                                            .split("/job/"))
                                    .toArray()[1].toString()

                            : postingURLShort.contains(ApplicationConstants.UNDP_PARTNER_AGENCIES_URL_IDENTIFIER) ?

                            ApplicationConstants.UNDP + "-" + Arrays.stream(Arrays.stream(rowCells.get(0)
                                                    .getElementsByTag("a")
                                                    .get(0).attr("href")
                                                    .split("="))
                                            .toArray()[1].toString().split("&"))
                                    .toArray()[0].toString()

                            : ApplicationConstants.UNDP + "-" + Arrays.stream(rowCells.get(0)
                                    .getElementsByTag("a")
                                    .get(0).attr("href")
                                    .split("="))
                            .toArray()[1].toString();

                    String postingURL = postingURLShort.contains(ApplicationConstants.UNDP_ORACLE_HCM_IDENTIFIER) ?
                            postingURLShort
                            : postingURLShort.contains(ApplicationConstants.UNDP_PARTNER_AGENCIES_URL_IDENTIFIER) ?
                            ApplicationConstants.UNDP_Peoplesoft_URL_PREFIX + Arrays.stream(postingURLShort.split("JobOpeningId")).toArray()[1].toString()
                                    .replace("HRS_JO_PST_SEQ", "PostingSeq")
                                    .replace("hrs_site_id", "SiteId")
                            : ApplicationConstants.UNDP_POSTING_LINK_URL_PREFIX + postingURLShort;
                    String undpjobLevel = rowCells.get(2).text();
                    String undpDeadline = rowCells.get(3).text();
                    String undpDutyStation = rowCells.get(4).text();

                    if (jobOpeningRepository.findByJobOpeningId(undpJobId).isEmpty()) {

                        DecimalFormat decimalFormat = new DecimalFormat("###");
                        if (j % 10 == 0) {
                            logger.info(i + " out of " + i_max + " %" + decimalFormat.format((j / j_double) * 100));


                        }
                        JobOpening jobOpening = JobOpening.builder()
                                .jobOpeningId(undpJobId)
                                .unEntity(ApplicationConstants.UNDP)
                                .level(undpjobLevel)
                                .postingUrl(postingURL)
                                .deadlineDate(undpDeadline)
                                .dutyStation(undpDutyStation)
                                .jobTitle(postingTitle.trim())
                                .postingDescrRaw(getAdditionalAttributesFromPostingPage(postingURL, undpJobId))
                                .addedDate(new Date())
                                .build();
                        counter++;
                        jobOpeningRepository.save(jobOpening);


                    }


                }

            }


        }


        JobOpeningLoadStatus loadStatus = JobOpeningLoadStatus.builder()
                .entity(ApplicationConstants.UNDP)
                .endDateTimestamp(new Date())
                .startDateTimestamp(startDate)
                .count(counter)
                .build();

        loadStatusRepository.save(loadStatus);

        logger.info(counter + " UNDP Jobs has been loaded!");

    }

    private String getAdditionalAttributesFromPostingPage(String url, String undpJobId) throws IOException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        String jobId = undpJobId.replaceAll(ApplicationConstants.UNDP + "-", "");

        if (url.contains(ApplicationConstants.UNDP_ORACLE_HCM_IDENTIFIER)) {

            String apiUrl = ApplicationConstants.UNDP_ORACLE_HCM_JOB_DESCR_API + jobId + "%22,siteNumber=CX_1";


            System.out.println(apiUrl);


            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();

            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>("parameters", httpHeaders);
            ResponseEntity<String> result = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);


            System.out.println(result);
            return result.toString();
        } else if (url.contains(ApplicationConstants.UNDP_PARTNER_AGENCIES_URL_IDENTIFIER)) {

            return postingPageDoc.select("#win0divPSPAGECONTAINER").text();

        } else {
            return postingPageDoc.select("#content-main").text();
        }
    }

}
