package com.example.aopdemo.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcuts {

    @Pointcut("execution(* com.example.aopdemo.service.GreetingService.sayHello(..)) || "
            + "execution(* com.example.aopdemo.service.ReportService.riskyOperation(..))")
    public void adviceTypeDemoMethods() {
    }
}
