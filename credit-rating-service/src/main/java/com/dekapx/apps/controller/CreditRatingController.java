package com.dekapx.apps.controller;

import com.dekapx.apps.model.CreditScore;
import com.dekapx.apps.service.CreditRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dekapx.apps.common.HttpStatusCodes.UNAUTHORIZED;
import static com.dekapx.apps.common.HttpStatusCodes.UNAUTHORIZED_MSG;
import static com.dekapx.apps.common.ResourceUrls.BASE_URL;
import static com.dekapx.apps.common.ResourceUrls.CREDIT_SCORE_BY_SSN_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@AllArgsConstructor
public class CreditRatingController {
    @Operation(summary = "CreditScore Service API Info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = UNAUTHORIZED,
                    description = UNAUTHORIZED_MSG,
                    content = @Content)})
    @GetMapping(INFO_URL)
    public String getInfo() {
        return "Credit Rating Service API v1.0";
    }

    @Operation(summary = "Find Credit Score by SSN in format [xxx-xx-xxxx]")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found Credit Score for SSN",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditScore.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Credit Score for SSN [x] not found.",
                    content = @Content)})
    @GetMapping(value = CREDIT_SCORE_BY_SSN_URL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditScore> getCreditScore(@PathVariable String ssn) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
