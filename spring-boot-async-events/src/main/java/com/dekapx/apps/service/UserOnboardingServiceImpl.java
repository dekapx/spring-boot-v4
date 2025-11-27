package com.dekapx.apps.service;

import com.dekapx.apps.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserOnboardingServiceImpl implements UserOnboardingService{
    @Override
    public void onboardUser(UserModel userModel) {
        log.info("Onboarding user... {}", userModel.getFirstName());
    }
}
