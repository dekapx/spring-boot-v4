package com.dekapx.apps.listener;

import com.dekapx.apps.event.UserOnboardingEvent;
import com.dekapx.apps.model.UserModel;
import com.dekapx.apps.service.UserOnboardingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserOnboardingEventListener {
    @Autowired
    private UserOnboardingService userOnboardingService;

    @Async
    @EventListener
    public void onApplicationEvent(UserOnboardingEvent<UserModel> event) {
        UserModel userModel = event.getModel();
        log.info("UserOnboardingEvent received... {}", userModel.getFirstName());
        this.userOnboardingService.onboardUser(userModel);
        log.info("UserOnboardingEvent processing completed... {}", userModel.getFirstName());
    }
}
