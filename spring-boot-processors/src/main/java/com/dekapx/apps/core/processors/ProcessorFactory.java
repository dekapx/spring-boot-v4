package com.dekapx.apps.core.processors;

public interface ProcessorFactory {
    Processor getProcessor(String beanName);
}

