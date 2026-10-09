package com.example.aopdemo.aspect;

import com.example.aopdemo.annotation.Auditable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CombinedPointcutAspect {

    @Before("execution(* com.example.aopdemo.service..*(..)) && @annotation(auditable)")
    public void logAuditableEvent(Auditable auditable) {
        log.info("  [execution && @annotation]  audit event recorded: {}", auditable.value());
    }
}
