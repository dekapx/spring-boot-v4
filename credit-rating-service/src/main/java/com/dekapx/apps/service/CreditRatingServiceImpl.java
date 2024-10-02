package com.dekapx.apps.service;

import com.dekapx.apps.model.CreditScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class CreditRatingServiceImpl implements CreditRatingService {
    @Override
    public CreditScore getCreditScore(String ssn) {
        return new CreditScore(
                ssn,
                "Dummy",
                "Person",
                LocalDate.of(1989, 2, 22),
                450);
    }
}
