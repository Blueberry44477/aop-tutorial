package com.example.aopdemo.service;

import com.example.aopdemo.annotation.Auditable;
import com.example.aopdemo.annotation.LogExecutionTime;
import com.example.aopdemo.annotation.RequiresRole;
import com.example.aopdemo.exception.RiskyOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportService {

    public void riskyOperation(boolean shouldFail) {
        log.info("  [business logic] Attempting risky operation (shouldFail={})", shouldFail);
        if (shouldFail) {
            throw new RiskyOperationException("Simulated failure, on purpose");
        }
        log.info("  [business logic] Risky operation succeeded");
    }

    @LogExecutionTime
    public void slowOperation() {
        log.info("  [business logic] Doing something slow...");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("  [business logic] Slow operation finished");
    }

    @RequiresRole("ADMIN")
    public void deleteAllData() {
        log.info("  [business logic] ALL DATA DELETED (not really - this is a demo)");
    }

    public void processNumber(int value) {
        log.info("  [business logic] Processing number {}", value);
    }

    @Auditable("REPORT_GENERATED")
    public void auditedAction() {
        log.info("  [business logic] Performing an audited action");
    }

    public void generateReport(String type) {
        log.info("  [business logic] Generating report of type {}", type);
    }

    /*
    * Self-invocation
    * */
    public void outerOperation() {
        log.info("  [business logic] outerOperation() starting - about to call");
        log.info("  [business logic] slowOperation() via \"this.\" (self-invocation)");
        this.slowOperation();
        log.info("  [business logic] outerOperation() finished");
    }
}
