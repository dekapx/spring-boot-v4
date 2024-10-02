package com.dekapx.apps.client;

import com.dekapx.apps.model.CreditScore;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.dekapx.apps.common.ResourceUrls.CREDIT_SCORE_BY_SSN_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@FeignClient(name = "creditScoreClient", url = "${feign.client.config.creditScoreClient.url}")
public interface CreditScoreClient {
    @GetMapping(INFO_URL)
    String getInfo();

    @GetMapping(CREDIT_SCORE_BY_SSN_URL)
    CreditScore getCreditScore(@PathVariable String ssn);
}

