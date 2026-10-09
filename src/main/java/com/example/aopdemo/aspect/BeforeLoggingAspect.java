package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class BeforeLoggingAspect {

    @Before("com.example.aopdemo.aspect.CommonPointcuts.adviceTypeDemoMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("  [@Before]         -> entering {} with args {}",
                joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }
}
