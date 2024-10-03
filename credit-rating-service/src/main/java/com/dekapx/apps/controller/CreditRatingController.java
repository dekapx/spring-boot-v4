package com.dekapx.apps.controller;

import com.dekapx.apps.model.CreditScoreRequest;
import com.dekapx.apps.model.CreditScoreResponse;
import com.dekapx.apps.service.CreditScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dekapx.apps.common.HttpStatusCodes.UNAUTHORIZED;
import static com.dekapx.apps.common.HttpStatusCodes.UNAUTHORIZED_MSG;
import static com.dekapx.apps.common.ResourceUrls.BASE_URL;
import static com.dekapx.apps.common.ResourceUrls.CREDIT_SCORE_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@AllArgsConstructor
public class CreditRatingController {
    private final CreditScoreService creditScoreService;

    @Operation(summary = "CreditScore Service API Info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = UNAUTHORIZED,
                    description = UNAUTHORIZED_MSG,
                    content = @Content)})
    @GetMapping(INFO_URL)
    public String getInfo() {
        log.info("Credit Rating Service API v1.0");
        return "Credit Rating Service API v1.0";
    }

    @PostMapping(value = CREDIT_SCORE_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditScoreResponse> getCreditScore(@RequestBody CreditScoreRequest request) {
        log.info("Credit Score request received for SSN [{}]", request.getSsn());
        final CreditScoreResponse response = this.creditScoreService.getCreditScore(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
