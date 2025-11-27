package com.dekapx.apps.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserModel {
    private String firstName;
    private String lastName;
    private LocalDateTime joiningDate;
}
