package com.dekapx.apps.user.processors;

import com.dekapx.apps.core.processors.Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEntitlmentsProcessor implements Processor {
    @Override
    public void process(Object[] args) {
        log.info("UserEntitlmentsProcessor...");
    }
}
