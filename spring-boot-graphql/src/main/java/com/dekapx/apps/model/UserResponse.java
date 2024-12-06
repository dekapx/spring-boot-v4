package com.dekapx.apps.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Data
@Getter
@Setter
public class UserResponse {
    private List<UserModel> users;
}
