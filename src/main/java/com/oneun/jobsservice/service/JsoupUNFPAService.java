package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.model.JobOpening;
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
import java.util.Arrays;
import java.util.Date;

import static com.oneun.jobsservice.Constants.ApplicationConstants.UNFPAJobsEndpointURL;

@Service
public class JsoupUNFPAService {

    Logger logger = LoggerFactory.getLogger(JsoupUNFPAService.class);
    @Autowired
    private final JobOpeningRepository jobOpeningRepository;

    @Autowired
    private final JobOpeningLoadStatusRepository loadStatusRepository;

    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    public JsoupUNFPAService(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository, JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository) {
        this.jobOpeningRepository = jobOpeningRepository;
        this.loadStatusRepository = loadStatusRepository;
        this.jobOpeningElasticSearchRepository = jobOpeningElasticSearchRepository;
    }


    public void parseUNFPACareers() throws IOException, SocketException, JSONException {



        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(UNFPAJobsEndpointURL, HttpMethod.GET,HttpEntity.EMPTY,String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());

        JSONObject jsonObjectItem = (JSONObject) jsonObject.getJSONArray("items").get(0);

        int totalJobCount = Integer.parseInt(jsonObjectItem.get("TotalJobsCount").toString());

//        System.out.println(totalJobCount/24);

        for (int i = 0; i < (totalJobCount / 24)+1; i++) {

            String pagedUrl = UNFPAJobsEndpointURL + ",offset="+(i*24);
            RestTemplate restTemplatePaged = new RestTemplate();


//            System.out.println(pagedUrl);
            ResponseEntity<String> pagedResponse = restTemplatePaged.exchange(pagedUrl, HttpMethod.GET,HttpEntity.EMPTY,String.class);


            JSONObject pagedJsonObject = new JSONObject(pagedResponse.getBody().toString());

            JSONObject pagedJsonObjectItem = (JSONObject) pagedJsonObject.getJSONArray("items").get(0);
            int totalJobCountPaged = pagedJsonObjectItem.getJSONArray("requisitionList").length();


            for (int j = 0; j < totalJobCountPaged; j++) {
                JSONObject requisition = (JSONObject) pagedJsonObjectItem.getJSONArray("requisitionList").get(j);

                String id = requisition.getString("Id");
                String unfpaJobId = ApplicationConstants.UNFPA+"-"+id;
                String unfpaPrimaryLocation=requisition.getString("PrimaryLocation");
                String unfpaJobTitle = requisition.getString("Title");
                String unfpaShortDescriptionAboutJob = requisition.getString("ShortDescriptionStr");
                String unfpaJobPreviewURL = ApplicationConstants.UNFPARequisitionURL+id;
                String unfpaRequisitionApiURL = ApplicationConstants.UNFPARequisitionApiURL+id;
//                System.out.println(unfpaRequisitionApiURL);

                JSONObject unfpaJobRequisition = getJobAttributes(unfpaRequisitionApiURL);
                JSONObject jobReqDetails = (JSONObject) unfpaJobRequisition.getJSONArray("items").get(0);
                JSONArray requisitionFlexFields = jobReqDetails.getJSONArray("requisitionFlexFields");

                int flexFieldsSize = requisitionFlexFields.length();

                String grade = null;
                for (int k = 0; k < flexFieldsSize; k++) {
                   if( requisitionFlexFields.getJSONObject(k).getString("Prompt").equals("Grade") ) {
                        grade = requisitionFlexFields.getJSONObject(k).getString("Value");
                        break;

                    }

                }

                String jobDescr = jobReqDetails.getString("ExternalDescriptionStr");


                if (unfpaJobId != null) {

                    if (jobOpeningRepository.findByJobOpeningId(unfpaJobId).isEmpty()){
                        JobOpening jobOpening = JobOpening.builder()
                                .jobOpeningId(unfpaJobId)
                                .dutyStation(unfpaPrimaryLocation)
                                .unEntity(ApplicationConstants.UNFPA)
                                .jobTitle(unfpaJobTitle)
                                .postingUrl(unfpaJobPreviewURL)
                                .unicefJobDescrBasic(unfpaShortDescriptionAboutJob)
                                .level(grade)
                                .postingDescrRaw(jobDescr.replaceAll("<[^>]*>", ""))
                                .addedDate(new Date())
                                .build();
//                        System.out.println(jobOpening);
                        jobOpeningRepository.save(jobOpening);


                    }

                    if (jobOpeningElasticSearchRepository.findByJobOpeningId(unfpaJobId).isEmpty()){
                        com.oneun.jobsservice.model.elastic.JobOpening jobOpeningES = com.oneun.jobsservice.model.elastic.JobOpening.builder()
                                .id(unfpaJobId)
                                .jobOpeningId(unfpaJobId)
                                .dutyStation(unfpaPrimaryLocation)
                                .unEntity(ApplicationConstants.UNFPA)
                                .jobTitle(unfpaJobTitle)
                                .postingUrl(unfpaJobPreviewURL)
                                .unicefJobDescrBasic(unfpaShortDescriptionAboutJob)
                                .level(grade)
                                .postingDescrRaw(jobDescr.replaceAll("<[^>]*>", ""))
                                .addedDate(new Date())
                                .build();
//                        System.out.println(jobOpeningES);
                        jobOpeningElasticSearchRepository.save(jobOpeningES);


                    }

                }



            }



        }


    }


    private JSONObject getJobAttributes(String api) throws JSONException {


        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> result = restTemplate.exchange(api, HttpMethod.GET, HttpEntity.EMPTY, String.class);

        return new JSONObject(result.getBody());
    }

}


