package com.dekapx.apps.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
