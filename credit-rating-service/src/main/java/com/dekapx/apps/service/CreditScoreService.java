package com.dekapx.apps.service;

import com.dekapx.apps.model.CreditScoreRequest;
import com.dekapx.apps.model.CreditScoreResponse;

public interface CreditScoreService {
    CreditScoreResponse getCreditScore(CreditScoreRequest request);
}
