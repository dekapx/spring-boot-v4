package com.dekapx.apps.handler;

import org.springframework.context.ApplicationEvent;

public interface EventHandler {
    void handleEvent(ApplicationEvent applicationEvent);
}
