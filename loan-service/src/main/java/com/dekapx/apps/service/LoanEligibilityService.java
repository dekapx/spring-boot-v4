package com.dekapx.apps.service;

import com.dekapx.apps.model.LoanEligibilityRequest;
import com.dekapx.apps.model.LoanEligibilityResponse;

public interface LoanEligibilityService {
    LoanEligibilityResponse checkLoanEligibility(LoanEligibilityRequest request);
}
