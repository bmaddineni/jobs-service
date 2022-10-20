package com.oneun.jobsservice.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ScheduleService {

    Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private JsoupUNSService jsoupUNSService;
    @Autowired
    private JsoupUNDPService jsoupUNDPService;
    @Autowired
    private JsoupWFPService jsoupWFPService;

    @Autowired
    private JsoupUNICEFService jsoupUNICEFService;
    @Autowired
    private JsoupUNESCOService jsoupUNESCOService;



//    @Scheduled(cron = "0 0 * * * *")
    void scheduleAll() throws IOException, JSONException {

        logger.info("Scheduling Service initialized!");

        jsoupUNSService.parseUNCareers();
        logger.info("UNS Parsing completed during schedule!");

        jsoupUNDPService.parseUNDPCareers();
        logger.info("UNDP Parsing completed during schedule!");

        jsoupWFPService.parseWFPCareers();
        logger.info("WFP Parsing completed during schedule!");

        jsoupUNICEFService.parseUNICEFCareers();
        logger.info("UNICEF Parsing completed during schedule!");

        jsoupUNESCOService.parseUNESCOCareers();
        logger.info("UNESCO Parsing completed during schedule!");


    }
}