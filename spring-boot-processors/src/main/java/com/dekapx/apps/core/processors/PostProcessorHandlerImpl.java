package com.dekapx.apps.core.processors;

import com.dekapx.apps.core.annotation.PostProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostProcessorHandlerImpl implements PostProcessorHandler {
    private final ProcessorExecutor processorExecutor;

    @Override
    public void postProcess(final ProceedingJoinPoint joinPoint, final PostProcessor postProcessor) {
        final Class[] processorTypes = postProcessor.processorTypes();
        final Object[] args = joinPoint.getArgs();
        this.processorExecutor.execute(processorTypes, args);
    }
}
