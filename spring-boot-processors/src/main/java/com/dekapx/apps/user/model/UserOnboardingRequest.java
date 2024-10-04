package com.dekapx.apps.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOnboardingRequest {
    private String firstName;
    private String lastNameName;
}
