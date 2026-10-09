package com.example.aopdemo.service;

/**
 * У цього інтерфейсу одна мета - дозволити спрінгу створити Проксі за допомогою JDK Dynamic Proxy.
 */
public interface GreetingService {
    String sayHello(String name);
}
