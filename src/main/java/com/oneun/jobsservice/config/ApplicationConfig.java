package com.oneun.jobsservice.config;

import com.oneun.jobsservice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

@Configuration
public class ApplicationConfig {
    @Bean
    RestTemplate restTemplate(){

//        return new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false); // Avoid buffering the request body to prevent memory issues
        requestFactory.setOutputStreaming(true); // Enable streaming of the request body

        // Set up the SSL context
        requestFactory.setConnectTimeout(5000); // Set connection timeout
        requestFactory.setReadTimeout(5000); // Set read timeout
        return new RestTemplate(requestFactory);

    }

    @Bean
    HttpHeaders headers(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.HOST, "careers.un.org");
        httpHeaders.set(HttpHeaders.CONTENT_LENGTH,"160");

        return httpHeaders;

    }


}
