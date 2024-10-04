package com.dekapx.apps.core.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class DummyProcessor implements Processor {
    @Override
    public void process(Object[] args) {
        log.info("DummyProcessor triggered...");
        Arrays.stream(args).forEach(o -> {
            log.info(o.toString());
        });
    }
}
