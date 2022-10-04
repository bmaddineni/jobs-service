package com.oneun.jobsservice.service;

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



    @Scheduled(cron = "0 0 * * * *")
    void scheduleAll() throws IOException {

        logger.info("Config class has been initialized during start up!");

        jsoupUNSService.parseUNCareers();
        logger.info("UNS Parsing completed during application start up! ");

        jsoupUNDPService.parseUNDPCareers();
        logger.info("UNDP Parsing completed during application start up! ");

//
        jsoupWFPService.parseWFPCareers();
        logger.info("WFP Parsing completed during application start up! ");
//
//
        jsoupUNICEFService.parseUNICEFCareers();
        logger.info("UNICEF Parsing completed during application start up! ");

    }
}