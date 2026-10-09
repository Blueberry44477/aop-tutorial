package com.example.aopdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ArgsPointcutAspect {

    @Before("execution(* com.example.aopdemo.service..*(..)) && args(value)")
    public void logIntArgument(int value) {
        log.info("  [args(int)]       matched a call with a single int argument: {}", value);
    }
}
