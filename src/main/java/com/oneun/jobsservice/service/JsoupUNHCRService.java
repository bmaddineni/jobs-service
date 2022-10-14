package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.dto.UNHCRRequest;
import com.oneun.jobsservice.helper.SSLHelper;
import com.oneun.jobsservice.model.JobOpening;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
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
public class JsoupUNHCRService {

    Logger logger = LoggerFactory.getLogger(JsoupUNHCRService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    public JsoupUNHCRService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
    }


    public void parseUNHCRCareers() throws IOException, SocketException, JSONException {

        String unhcrEndpointURL = "https://unhcr.wd3.myworkdayjobs.com/wday/cxs/unhcr/External/jobs";

        String unhcrUrlPrefix = "https://unhcr.wd3.myworkdayjobs.com/en-US/External";

        String unhcrApiEndpointPrefix = "https://unhcr.wd3.myworkdayjobs.com/wday/cxs/unhcr/External";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<UNHCRRequest> request = new HttpEntity<>(new UNHCRRequest(""));
        System.out.println(request);
        ResponseEntity<String> response = restTemplate.exchange(unhcrEndpointURL, HttpMethod.POST,request,String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        int total = Integer.parseInt(jsonObject.get("total").toString());


        JSONArray jsonArray = new JSONArray(jsonObject.get("jobPostings").toString());

        for (int i = 0; i < total; i++) {

            System.out.println(jsonArray.getJSONObject(i));
            String externalPath = jsonArray.getJSONObject(i).getString("externalPath");
            JSONArray bulletedFields = jsonArray.getJSONObject(i).getJSONArray("bulletFields");

            String jobId = null;
            String level = null;

            if (bulletedFields.length() == 1 || bulletedFields.length() == 2)
            {
                 jobId = ApplicationConstants.UNHCR+"-"+bulletedFields.get(0).toString();
            }

            if (bulletedFields.length() == 3)
            {
                 level = bulletedFields.get(1).toString();

                jobId = ApplicationConstants.UNHCR+"-"+bulletedFields.get(0).toString();
            }


            String dutyStation = jsonArray.getJSONObject(i).get("locationsText").toString();
            String url = unhcrUrlPrefix+externalPath;
            String jobDetailsApiUrl = unhcrApiEndpointPrefix+externalPath;

            String title = jsonArray.getJSONObject(i).get("title").toString();



            RestTemplate restTemplate1 = new RestTemplate();
            HttpHeaders headers1 = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<UNHCRRequest> request1 = new HttpEntity<>(new UNHCRRequest(""));
//            System.out.println(request);
            ResponseEntity<String> response1 = restTemplate.exchange(jobDetailsApiUrl, HttpMethod.GET,HttpEntity.EMPTY,String.class);

            JSONObject jsonJobPosting = new JSONObject(response1.getBody());

            String jobPostingDescr = jsonJobPosting.getJSONObject("jobPostingInfo").getString("jobDescription");
//            System.out.println();

            if (jobOpeningRepository.findByJobOpeningId(jobId).isEmpty()) {

                JobOpening jobOpening = JobOpening.builder()
                        .jobOpeningId(jobId)
                        .level(level)
                        .jobTitle(title)
                        .postingUrl(url)
                        .dutyStation(dutyStation)
                        .postingDescrRaw(jobPostingDescr)
                        .addedDate(new Date())
                        .unEntity(ApplicationConstants.UNHCR)
                        .build();

                jobOpeningRepository.save(jobOpening);
            }

        }


    }

    private String getAdditionalAttributesFromPostingPage(String url) throws IOException , SocketException {
        Document postingPageDoc = SSLHelper.getConnection(url).get();

        return postingPageDoc.getElementsByClass("jobdescription").text();

    }


    public void testUNDPHCMAPI() {

        String apiUNDP="https://estm.fa.em2.oraclecloud.com/hcmRestApi/resources/latest/recruitingCEJobRequisitionDetails?expand=all&onlyData=true&finder=ById;Id=2745";

        RestTemplate restTemplate1 = new RestTemplate();
        HttpHeaders headers1 = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<UNHCRRequest> request1 = new HttpEntity<>(new UNHCRRequest(""));
//            System.out.println(request);
        ResponseEntity<String> response1 = restTemplate1.exchange(apiUNDP, HttpMethod.GET,HttpEntity.EMPTY,String.class);

        System.out.println(response1);

    }
}
