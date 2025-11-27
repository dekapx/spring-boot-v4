package com.dekapx.apps.controller;

import com.dekapx.apps.event.UserOnboardingEvent;
import com.dekapx.apps.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserOnboardController {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void onboardUser(UserModel userModel) {
        UserOnboardingEvent<UserModel> userOnboardingEvent = prepareUserOnboardingEvent(userModel);
        eventPublisher.publishEvent(userOnboardingEvent);
    }

    private UserOnboardingEvent<UserModel> prepareUserOnboardingEvent(UserModel userModel) {
        return new UserOnboardingEvent(this, userModel);
    }
}
