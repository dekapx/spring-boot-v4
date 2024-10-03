package com.dekapx.apps.aspect;

import com.dekapx.apps.annotation.EventInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class EventInterceptorAspect {
    @Around("@annotation(eventInterceptor)")
    public Object around(final ProceedingJoinPoint joinPoint, final EventInterceptor eventInterceptor) throws Throwable {
        final String event = eventInterceptor.event();
        log.info("Event Name: [{}]", event);
        return joinPoint.proceed();
    }
}
