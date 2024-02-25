package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.dto.UNHCRRequest;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.model.JobpostingStatus;
import com.oneun.jobsservice.repository.JobOpeningElasticSearchRepository;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

@Service
public class JsoupGEService {

    Logger logger = LoggerFactory.getLogger(JsoupGEService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    @Autowired
    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    @Autowired
    private RestTemplate restTemplate;
    public JsoupGEService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository, JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
        this.jobOpeningElasticSearchRepository = jobOpeningElasticSearchRepository;
    }


    public void parseCareers() throws IOException, SocketException, JSONException {

        String imfEndpointURL = "https://ge.wd5.myworkdayjobs.com/wday/cxs/ge/GE_ExternalSite/jobs";
//        https://ge.wd5.myworkdayjobs.com/wday/cxs/ge/GE_ExternalSite/jobs


        String imfUrlPrefix = "https://ge.wd5.myworkdayjobs.com/en-US/GE_ExternalSite";

        String imfApiEndpointPrefix = "https://ge.wd5.myworkdayjobs.com/wday/cxs/ge/GE_ExternalSite";

//        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<UNHCRRequest> request = new HttpEntity<>(new UNHCRRequest(""));
//        System.out.println(request);
        ResponseEntity<String> response = restTemplate.exchange(imfEndpointURL, HttpMethod.POST,request,String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        int total = Integer.parseInt(jsonObject.get("total").toString());


        JSONArray jsonArray = new JSONArray(jsonObject.get("jobPostings").toString());

        for (int i = 0; i < jsonArray.length(); i++) {

//            System.out.println(jsonArray.getJSONObject(i));
            String externalPath = jsonArray.getJSONObject(i).getString("externalPath");
            JSONArray bulletedFields = jsonArray.getJSONObject(i).getJSONArray("bulletFields");


            System.out.println(bulletedFields.toString());

            String    jobId = ApplicationConstants.GE+"-"+bulletedFields.get(0).toString();
           String deadLine = bulletedFields.length()>1? bulletedFields.get(1).toString().replace("The last day to apply to this job is :","")
                   .replace("- Closing based on :","") : "";





            String dutyStation = jsonArray.getJSONObject(i).get("locationsText").toString();
            String url = imfUrlPrefix+externalPath;
            String jobDetailsApiUrl = imfApiEndpointPrefix+externalPath;

            String title = jsonArray.getJSONObject(i).get("title").toString();



            RestTemplate restTemplate1 = new RestTemplate();

            ResponseEntity<String> response1 = restTemplate1.exchange(jobDetailsApiUrl, HttpMethod.GET,HttpEntity.EMPTY,String.class);

            JSONObject jsonJobPosting = new JSONObject(response1.getBody());

            String jobPostingDescr = jsonJobPosting.getJSONObject("jobPostingInfo").getString("jobDescription");
//
            if (jobId != null) {


            if ( jobOpeningRepository.findByJobOpeningId(jobId).isEmpty()) {

                JobOpening jobOpening = JobOpening.builder()
                        .jobOpeningId(jobId)
                        .level(null)
                        .jobTitle(title)
                        .postingUrl(url)
                        .deadlineDate(deadLine)
                        .dutyStation(dutyStation)
                        .postingDescrRaw(jobPostingDescr)
                        .addedDate(new Date())
                        .unEntity("GE")
                        .jobpostingStatus(JobpostingStatus.ACTIVE)

                        .build();

                jobOpeningRepository.save(jobOpening);
            } else {

                JobOpening jobOpening = JobOpening.builder()
                        .id(jobOpeningRepository.findByJobOpeningId(jobId).get(0).getId())
                        .jobpostingStatus(JobpostingStatus.IN_ACTIVE)

                        .build();

                jobOpeningRepository.save(jobOpening);

            }

                if ( jobOpeningElasticSearchRepository.findByJobOpeningId(jobId).isEmpty()) {

                    com.oneun.jobsservice.model.elastic.JobOpening jobOpeningES = com.oneun.jobsservice.model.elastic.JobOpening.builder()
                            .id(jobId)
                            .jobOpeningId(jobId)
                            .level(null)
                            .jobTitle(title)
                            .postingUrl(url)
                            .deadlineDate(deadLine)
                            .dutyStation(dutyStation)
                            .postingDescrRaw(jobPostingDescr)
                            .addedDate(new Date())
                            .unEntity(ApplicationConstants.GE)
                            .jobpostingStatus(JobpostingStatus.ACTIVE)

                            .build();

                    jobOpeningElasticSearchRepository.save(jobOpeningES);
                }
            }

        }


    }


    }


