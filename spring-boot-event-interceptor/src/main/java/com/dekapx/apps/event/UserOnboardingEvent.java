package com.dekapx.apps.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserOnboardingEvent<T> extends ApplicationEvent {
    private T model;
    public UserOnboardingEvent(Object source, T model) {
        super(source);
        this.model = model;
    }

    public T getModel() {
        return model;
    }
}
