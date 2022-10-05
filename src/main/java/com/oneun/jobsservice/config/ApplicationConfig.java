package com.oneun.jobsservice.config;

import com.oneun.jobsservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
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


    @Bean
    CommandLineRunner init(){
        return args -> {
            logger.info("Config class has been initialized during start up!");


//            jsoupUNSService.parseUNCareers();
//            logger.info("UNS Parsing completed during application start up! ");
//
            jsoupUNDPService.parseUNDPCareers();
            logger.info("UNDP Parsing completed during application start up! ");
//
////
//            jsoupWFPService.parseWFPCareers();
//            logger.info("WFP Parsing completed during application start up! ");
////
////
//            jsoupUNICEFService.parseUNICEFCareers();
//            logger.info("UNICEF Parsing completed during application start up! ");


            jsoupUNESCOService.parseUNESCOCareers();
            logger.info("UNESCO Parsing completed during schedule!");




        };
    }
}
