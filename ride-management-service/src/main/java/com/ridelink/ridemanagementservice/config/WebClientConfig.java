package com.ridelink.ridemanagementservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Base clients for interservice REST calls. Actual usage (fetching eligible
 * drivers, notifying Fare Service on completion) is wired in Step 6.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient driverServiceWebClient(@Value("${driver.service.url}") String driverServiceUrl) {
        return WebClient.builder().baseUrl(driverServiceUrl).build();
    }

    @Bean
    public WebClient fareServiceWebClient(@Value("${fare.service.url}") String fareServiceUrl) {
        return WebClient.builder().baseUrl(fareServiceUrl).build();
    }
}
