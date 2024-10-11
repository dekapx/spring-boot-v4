package com.dekapx.apps.config;

import feign.Feign;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration is for LoadBalancer.
 * For Load Balancer verification, run multiple instances of credit-rating-service
 * on different ports e.g. [8082, 8083]
 */
@Configuration
@LoadBalancerClient(value = "credit-rating-service")
public class LoadBalancerConfig {
    @Bean
    @LoadBalanced
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
