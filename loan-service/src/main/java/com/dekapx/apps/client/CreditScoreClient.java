package com.dekapx.apps.client;

import com.dekapx.apps.model.CreditScoreRequest;
import com.dekapx.apps.model.CreditScoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.dekapx.apps.common.ResourceUrls.CREDIT_SCORE_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@FeignClient(name = "creditScoreClient", url = "${feign.client.config.creditScoreClient.url}")
public interface CreditScoreClient {
    @GetMapping(INFO_URL)
    String getInfo();

    @PostMapping(value = CREDIT_SCORE_URL)
    ResponseEntity<CreditScoreResponse> getCreditScore(@RequestBody CreditScoreRequest request);
}

