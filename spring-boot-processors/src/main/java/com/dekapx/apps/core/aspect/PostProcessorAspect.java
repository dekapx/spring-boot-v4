package com.dekapx.apps.core.aspect;

import com.dekapx.apps.core.annotation.PostProcessor;
import com.dekapx.apps.core.processors.PostProcessorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PostProcessorAspect {
    private final PostProcessorHandler postProcessorHandler;

    @Around("@annotation(postProcessor)")
    public void around(final ProceedingJoinPoint joinPoint, final PostProcessor postProcessor) throws Throwable {
        joinPoint.proceed();
        log.info("Perform PostProcessor...");
        this.postProcessorHandler.postProcess(joinPoint, postProcessor);
    }
}