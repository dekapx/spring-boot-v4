package com.dekapx.apps.service;

import com.dekapx.apps.model.CreditScoreRequest;
import com.dekapx.apps.model.CreditScoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditScoreServiceImpl implements CreditScoreService {
    @Override
    public CreditScoreResponse getCreditScore(CreditScoreRequest request) {
        log.info("Find and prepare CreditScore for SSN: [{}]", request.getSsn());
        return CreditScoreResponse.builder()
                .ssn(request.getSsn())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .creditScore(540)
                .build();
    }
}
