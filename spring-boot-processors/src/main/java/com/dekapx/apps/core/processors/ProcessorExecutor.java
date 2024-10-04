package com.dekapx.apps.core.processors;

public interface ProcessorExecutor {
    void execute(Class[] processorTypes, Object[] args);
}
