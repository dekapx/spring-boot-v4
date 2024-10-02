package com.dekapx.apps.model;

import java.time.LocalDate;

public record CreditScore(
        String ssn,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Integer creditScore) {
}
