package com.dekapx.apps.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreditScoreResponse {
    private String ssn;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer creditScore;
}
