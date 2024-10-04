package com.dekapx.apps.core.processors;

import com.dekapx.apps.core.annotation.PostProcessor;
import org.aspectj.lang.ProceedingJoinPoint;

public interface PostProcessorHandler {
    void postProcess(final ProceedingJoinPoint joinPoint, final PostProcessor postProcessor);
}
