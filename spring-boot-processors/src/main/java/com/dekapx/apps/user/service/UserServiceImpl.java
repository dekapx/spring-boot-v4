package com.dekapx.apps.user.service;

import com.dekapx.apps.core.annotation.PostProcessor;
import com.dekapx.apps.core.annotation.PreProcessor;
import com.dekapx.apps.user.model.UserOnboardingRequest;
import com.dekapx.apps.user.processors.BackgroundCheckProcessor;
import com.dekapx.apps.user.processors.UserAccountSetupProcessor;
import com.dekapx.apps.user.processors.UserEntitlmentsProcessor;
import com.dekapx.apps.user.processors.UserPayrollSetupProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    @PreProcessor(processorTypes = {BackgroundCheckProcessor.class})
    @PostProcessor(processorTypes = {UserAccountSetupProcessor.class, UserEntitlmentsProcessor.class, UserPayrollSetupProcessor.class})
    public void userOnboarding(UserOnboardingRequest request) {
        log.info("Perform UserOnboarding process for [{}]...", request.getFirstName());
    }
}
