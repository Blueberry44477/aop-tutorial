package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class WithinAspect {

    @Before("within(com.example.aopdemo.service.ReportService)")
    public void logWithinReportService(JoinPoint joinPoint) {
        log.info("  [within(ReportService)]  call intercepted inside ReportService: {}",
                joinPoint.getSignature().toShortString());
    }
}
