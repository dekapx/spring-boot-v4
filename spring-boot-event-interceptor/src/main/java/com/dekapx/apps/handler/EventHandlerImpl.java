package com.dekapx.apps.handler;

import com.dekapx.apps.annotation.EventInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandlerImpl implements EventHandler {
    @Override
    @EventInterceptor(event = "UserOnboardingEvent")
    public void handleEvent(ApplicationEvent applicationEvent) {
        log.info("Event Handler invoked...");
    }
}
