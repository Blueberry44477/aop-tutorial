package com.example.aopdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GreetingServiceImpl implements GreetingService {

    @Override
    public String sayHello(String name) {
        log.info("  [business logic] Saying hello to {}", name);
        return "Hello, " + name;
    }
}
