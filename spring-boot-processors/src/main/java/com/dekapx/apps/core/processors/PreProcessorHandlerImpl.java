package com.dekapx.apps.core.processors;

import com.dekapx.apps.core.annotation.PreProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreProcessorHandlerImpl implements PreProcessorHandler {
    private final ProcessorExecutor processorExecutor;

    @Override
    public void preProcess(final ProceedingJoinPoint joinPoint, final PreProcessor preProcessor) {
        final Class[] processorTypes = preProcessor.processorTypes();
        final Object[] args = joinPoint.getArgs();
        this.processorExecutor.execute(processorTypes, args);
    }
}
