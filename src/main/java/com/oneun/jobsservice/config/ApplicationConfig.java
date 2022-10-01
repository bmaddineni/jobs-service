package com.oneun.jobsservice.config;

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
    JsoupUNSService jsoupUNSService;
@Bean
    CommandLineRunner init(){
        return args -> {
            logger.info("Config class during start up!");
            System.out.println( "Config class during start up!");

            jsoupUNSService.parseUNCareers();
        };
    }
}
