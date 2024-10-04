package com.dekapx.apps.core.aspect;

import com.dekapx.apps.core.annotation.PreProcessor;
import com.dekapx.apps.core.processors.PreProcessorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class PreProcessorAspect {
    private final PreProcessorHandler preProcessorHandler;

    @Around("@annotation(preProcessor)")
    public void around(final ProceedingJoinPoint joinPoint, final PreProcessor preProcessor) throws Throwable {
        log.info("Perform PreProcessor...");
        this.preProcessorHandler.preProcess(joinPoint, preProcessor);
        joinPoint.proceed();
    }
}
