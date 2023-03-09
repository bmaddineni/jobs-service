package com.oneun.jobsservice;

import com.oneun.jobsservice.helper.LinkedInApi;
import com.oneun.jobsservice.helper.TwitterApi;
import com.oneun.jobsservice.service.*;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class JobsServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(JobsServiceApplication.class, args);
	}

	Logger logger = LoggerFactory.getLogger(JobsServiceApplication.class);

	@Autowired
	private JsoupIMFService jsoupIMFService;

	@Autowired
	private JsoupUNHCRService jsoupUNHCRService;
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

//	@Autowired
//	private JsoupILOService jsoupILOService;

	@Autowired
	private JsoupUNFPAService jsoupUNFPAService;

	@Autowired
	private LinkedInApi linkedInApi;

	@Autowired
	private TwitterApi twitterApi;


	@Bean
	CommandLineRunner init(){
		return args -> {
			logger.info("Config class has been initialized during start up!");

//			System.out.println(linkedInApi.submitLinkedInPost(new Date().toString()+": Hello From United Nations Jobs! "));

			Date dateStarted = new Date();


			logger.info("UNS Parsing started! ");

			jsoupUNSService.parseUNCareers();
			logger.info("UNS Parsing completed during application start up! ");

			logger.info("UNDP Parsing started! ");

			jsoupUNDPService.parseUNDPCareers();
			logger.info("UNDP Parsing completed during application start up! ");

			logger.info("WFP Parsing started! ");

			jsoupWFPService.parseWFPCareers();
			logger.info("WFP Parsing completed during application start up! ");


			logger.info("UNESCO Parsing started! ");
			jsoupUNESCOService.parseUNESCOCareers();
			logger.info("UNESCO Parsing completed during schedule!");


			logger.info("UNHCR loading started");
//			jsoupUNHCRService.testUNDPHCMAPI();
			jsoupUNHCRService.parseUNHCRCareers();

			logger.info("process started at : " + dateStarted);
			logger.info("UNICEF Parsing started! ");

			jsoupUNICEFService.parseUNICEFCareers();
			logger.info("UNICEF Parsing completed during application start up! ");


			logger.info("UNFPA loading started");
			jsoupUNFPAService.parseUNFPACareers();

			logger.info("ILO loading started");
//			jsoupILOService.parseILOCareers();

			logger.info("IMF loading started");
			jsoupIMFService.parseIMFCareers();




			Date dateEnded = new Date();


			long diffInMillies = Math.abs(dateStarted.getTime() - dateEnded.getTime());
			long diffHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			long diffMinutes = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			long diffSeconds = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);


			logger.info("process started at : " + dateStarted);
			logger.info("process ended at: " + dateEnded);

			logger.info("It took both elastic and mysql load (hours) :" + diffHours );
			logger.info("It took both elastic and mysql load (minutes) :" + diffMinutes );

			logger.info("It took both elastic and mysql load (seconds) :" + diffSeconds );



		};
	}

}
