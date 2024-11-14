package com.dekapx.apps.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class PollerConfig {
    @Bean
    public IntegrationFlow pollerFlow() {
        return IntegrationFlow
                .from()
                .get();
    }
}
