package com.oneun.jobsservice.service;

import com.oneun.jobsservice.Constants.ApplicationConstants;
import com.oneun.jobsservice.dto.UNHCRRequest;
import com.oneun.jobsservice.repository.JobOpeningElasticSearchRepository;
import com.oneun.jobsservice.repository.JobOpeningLoadStatusRepository;
import com.oneun.jobsservice.repository.JobOpeningRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JsoupUNSService1 {

    Logger logger = LoggerFactory.getLogger(JsoupUNSService1.class);
    @Autowired
    private JobOpeningRepository jobOpeningRepository;
    @Autowired
    private JobOpeningLoadStatusRepository loadStatusRepository;

    @Autowired
    private JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders headers;

    public JsoupUNSService1(JobOpeningRepository jobOpeningRepository, JobOpeningLoadStatusRepository loadStatusRepository, JobOpeningElasticSearchRepository jobOpeningElasticSearchRepository) {
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
    public void parseUNCareers() throws JSONException {


        int counter = 0;
        HttpEntity<UNHCRRequest> request = new HttpEntity<>(new UNHCRRequest(""));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("filterConfig",new JSONObject());
        JSONObject paginationObject = new JSONObject();
        paginationObject.put("page",0);
        paginationObject.put("itemPerPage",100);
        paginationObject.put("sortBy","startDate");
        paginationObject.put("sortDirection",-1);
        jsonObject.put("pagination", paginationObject);
        HttpEntity<String> req = new HttpEntity<>(jsonObject.toString());


        ResponseEntity<String> response = restTemplate.exchange("https://careers.un.org/api/public/opening/jo/list/filteredV2/en", HttpMethod.POST,req,String.class);

        System.out.println(response);


        }


    }







