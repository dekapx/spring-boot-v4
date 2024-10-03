package com.dekapx.apps.publisher;

import com.dekapx.apps.event.UserOnboardingEvent;
import com.dekapx.apps.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisherImpl implements EventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishEvent() {
        log.info("Prepare and publish event...");
        UserOnboardingEvent<UserModel> userOnboardingEvent = prepareUserOnboardingEvent(userModelSupplier.get());
        applicationEventPublisher.publishEvent(userOnboardingEvent);
    }

    private UserOnboardingEvent<UserModel> prepareUserOnboardingEvent(UserModel userModel) {
        return new UserOnboardingEvent(this, userModel);
    }

    private Supplier<UserModel> userModelSupplier = () ->
            UserModel.builder()
                    .firstName("Test")
                    .lastName("User")
                    .joiningDate(LocalDateTime.now())
                    .build();
}
