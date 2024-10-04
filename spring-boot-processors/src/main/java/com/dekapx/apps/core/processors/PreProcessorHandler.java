package com.dekapx.apps.core.processors;

import com.dekapx.apps.core.annotation.PreProcessor;
import org.aspectj.lang.ProceedingJoinPoint;

public interface PreProcessorHandler {
    void preProcess(final ProceedingJoinPoint joinPoint, final PreProcessor preProcessor);
}
