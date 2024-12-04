package com.dekapx.apps.repository;

import com.dekapx.apps.entity.User;

public class UserTestData {
    public static final String USERNAME = "Test001";
    public static final String FIRST_NAME = "Test";
    public static final String LAST_NAME = "User";
    public static final String EMAIL = "test.user@google.com";

    public static User createUser() {
        return User.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();
    }
}
