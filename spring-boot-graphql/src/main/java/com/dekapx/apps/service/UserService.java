package com.dekapx.apps.service;

import com.dekapx.apps.model.UserModel;

import java.util.List;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(Long Id);
}