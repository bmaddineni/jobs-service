package com.oneun.jobsservice;

import com.oneun.jobsservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobsServiceApplication {

	public JobsServiceApplication(JsoupUNSService jsoupUNSService, JsoupUNDPService jsoupUNDPService, JsoupWFPService jsoupWFPService, JsoupUNICEFService jsoupUNICEFService, JsoupUNESCOService jsoupUNESCOService) {
		this.jsoupUNSService = jsoupUNSService;
		this.jsoupUNDPService = jsoupUNDPService;
		this.jsoupWFPService = jsoupWFPService;
		this.jsoupUNICEFService = jsoupUNICEFService;
		this.jsoupUNESCOService = jsoupUNESCOService;
	}

	public static void main(String[] args) {
		SpringApplication.run(JobsServiceApplication.class, args);
	}

	Logger logger = LoggerFactory.getLogger(JobsServiceApplication.class);
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


			jsoupUNSService.parseUNCareers();
			logger.info("UNS Parsing completed during application start up! ");

			jsoupUNDPService.parseUNDPCareers();
			logger.info("UNDP Parsing completed during application start up! ");

			jsoupWFPService.parseWFPCareers();
			logger.info("WFP Parsing completed during application start up! ");


			jsoupUNICEFService.parseUNICEFCareers();
			logger.info("UNICEF Parsing completed during application start up! ");


			jsoupUNESCOService.parseUNESCOCareers();
			logger.info("UNESCO Parsing completed during schedule!");




		};
	}

}
