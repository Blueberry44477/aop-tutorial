package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AfterLoggingAspect {

    @After("com.example.aopdemo.aspect.CommonPointcuts.adviceTypeDemoMethods()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("  [@After]          <- exiting {} (runs regardless of outcome)",
                joinPoint.getSignature().toShortString());
    }
}
