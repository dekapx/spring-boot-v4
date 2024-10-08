package com.dekapx.apps.service;

import com.dekapx.apps.client.CreditScoreClient;
import com.dekapx.apps.model.CreditScoreRequest;
import com.dekapx.apps.model.CreditScoreResponse;
import com.dekapx.apps.model.LoanEligibilityRequest;
import com.dekapx.apps.model.LoanEligibilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.dekapx.apps.model.ApplicationStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanEligibilityServiceImpl implements LoanEligibilityService {
    private final CreditScoreClient creditScoreClient;

    @Override
    public LoanEligibilityResponse checkLoanEligibility(LoanEligibilityRequest request) {
        CreditScoreRequest creditScoreRequest = toCreditScoreRequest(request);
        ResponseEntity<CreditScoreResponse> responseEntity = this.creditScoreClient.getCreditScore(creditScoreRequest);
        return toLoanEligibilityResponse(responseEntity.getBody());
    }

    private CreditScoreRequest toCreditScoreRequest(LoanEligibilityRequest request) {
        return CreditScoreRequest.builder()
                .ssn(request.getSsn())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    private LoanEligibilityResponse toLoanEligibilityResponse(CreditScoreResponse response) {
        return LoanEligibilityResponse.builder()
                .ssn(response.getSsn())
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .dateOfBirth(response.getDateOfBirth())
                .applicationStatus(PENDING)
                .build();
    }
}
