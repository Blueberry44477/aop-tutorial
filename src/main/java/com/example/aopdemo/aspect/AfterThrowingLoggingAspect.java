package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AfterThrowingLoggingAspect {

    @AfterThrowing(pointcut = "com.example.aopdemo.aspect.CommonPointcuts.adviceTypeDemoMethods()",
                    throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.info("  [@AfterThrowing]  x  {} threw: {}: {}",
                joinPoint.getSignature().toShortString(), ex.getClass().getSimpleName(), ex.getMessage());
    }
}
