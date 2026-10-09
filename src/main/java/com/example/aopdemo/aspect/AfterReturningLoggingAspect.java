package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AfterReturningLoggingAspect {

    @AfterReturning("com.example.aopdemo.aspect.CommonPointcuts.adviceTypeDemoMethods()")
    public void logAfterReturning(JoinPoint joinPoint) {
        log.info("  [@AfterReturning]    {} completed successfully",
                joinPoint.getSignature().toShortString());
    }
}
