package com.dekapx.apps.controller;

import com.dekapx.apps.model.LoanEligibilityRequest;
import com.dekapx.apps.model.LoanEligibilityResponse;
import com.dekapx.apps.service.LoanEligibilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;
import static com.dekapx.apps.common.ResourceUrls.LOAN_ELIGIBITY_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class LoanEligibilityController {
    private final LoanEligibilityService loanEligibilityService;

    @Operation(summary = "Loan Service API Info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = UNAUTHORIZED,
                    description = UNAUTHORIZED_MSG,
                    content = @Content)})
    @GetMapping(INFO_URL)
    public String getInfo() {
        log.info("Loan Service API v1.0");
        return "Loan Service API v1.0";
    }

    @PostMapping(value = LOAN_ELIGIBITY_URL,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoanEligibilityResponse> checkLoanEligibility(@RequestBody LoanEligibilityRequest request) {
        log.info("Loan Eligibility request received for SSN [{}]", request.getSsn());
        final LoanEligibilityResponse response = this.loanEligibilityService.checkLoanEligibility(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
