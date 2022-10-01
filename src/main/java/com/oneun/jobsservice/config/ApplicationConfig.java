package com.oneun.jobsservice.config;

import com.oneun.jobsservice.service.JsoupUNDPService;
import com.oneun.jobsservice.service.JsoupUNSService;
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

    @Bean
    CommandLineRunner init(){
        return args -> {
            logger.info("Config class has been initialized during start up!");

            jsoupUNSService.parseUNCareers();
            logger.info("UNS Parsing completed during application start up! ");

            jsoupUNDPService.parseUNDPCareers();
            logger.info("UNDP Parsing completed during application start up! ");

        };
    }
}
