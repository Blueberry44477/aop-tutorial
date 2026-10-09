package com.example.aopdemo;

import com.example.aopdemo.context.CurrentUserContext;
import com.example.aopdemo.exception.AccessDeniedDemoException;
import com.example.aopdemo.exception.RiskyOperationException;
import com.example.aopdemo.service.GreetingService;
import com.example.aopdemo.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
public class AopDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AopDemoApplication.class, args);
    }

    @Component
    static class TutorialRunner implements CommandLineRunner {

        private final GreetingService greetingService;
        private final ReportService reportService;

        TutorialRunner(GreetingService greetingService, ReportService reportService) {
            this.greetingService = greetingService;
            this.reportService = reportService;
        }

        @Override
        public void run(String... args) {


            /* Proxy.

             * Приклад із двома механізмами створення різних проксі.
             * Важливий момент в тому, що JDK Proxy треба вмикати у application.yml
             * */
            log.info("  AopUtils.isJdkDynamicProxy(greetingService) = {}",
                    AopUtils.isJdkDynamicProxy(greetingService));

            log.info("  AopUtils.isCglibProxy(reportService)        = {}",
                    AopUtils.isCglibProxy(reportService));

            log.info("");


            /*
             * Advice типи.
             */
            log.info("Success path: sayHello(...)");
            greetingService.sayHello("World");

            log.info("");
            log.info("Failure path: riskyOperation(true)");
            try {
                reportService.riskyOperation(true);
            } catch (RiskyOperationException e) {
                log.info("  [runner] caught expected exception: {}", e.getMessage());
            }

            /*
            * Pointcut типи.
            * */
            log.info("@Around + @annotation()");
            reportService.slowOperation();

            log.info("Access-Control Aspect");
            log.info("USER role");
            CurrentUserContext.setRole("USER");
            try {
                reportService.deleteAllData();
            } catch (AccessDeniedDemoException e) {
                log.info("  [runner] caught expected exception: {}", e.getMessage());
            }

            log.info("");
            log.info("ADMIN role");
            CurrentUserContext.setRole("ADMIN");
            reportService.deleteAllData();
            CurrentUserContext.clear();

            log.info("Pointcut Expressions");
            reportService.processNumber(42);

            log.info("COMBINING POINTCUTS");
            reportService.auditedAction();

            log.info("SELF-INVOCATION");
            reportService.outerOperation();
            log.info("No [@Around/timing] lines above the business-logic lines this time -");

            log.info("within()");
            reportService.generateReport("QUARTERLY");

            log.info("  Done");
        }
    }
}
