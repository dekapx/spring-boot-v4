package com.dekapx.apps.service;

import com.dekapx.apps.model.CreditScore;

public interface CreditRatingService {
    CreditScore getCreditScore(String ssn);
}
