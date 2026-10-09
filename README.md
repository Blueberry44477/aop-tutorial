# Spring Boot AOP Tutorial (`aop-tutorial`)

Welcome to the **Aspect-Oriented Programming (AOP) Tutorial** in Spring Boot. This repository is a standalone, focused sandbox designed to help you master every essential AOP concept through working, observable code with zero unnecessary business logic.

---

## Table of Contents

1. [What is Aspect-Oriented Programming?](#what-is-aspect-oriented-programming)
2. [Project Architecture & File Map](#project-architecture--file-map)
3. [Prerequisites & How to Run](#prerequisites--how-to-run)
4. [Step-by-Step Learning Guide](#step-by-step-learning-guide)
   - [Step 1: Proxy Mechanics (JDK Dynamic Proxy vs. CGLIB)](#step-1-proxy-mechanics-jdk-dynamic-proxy-vs-cglib)
   - [Step 2: The Advice Lifecycle (Success vs. Failure Paths)](#step-2-the-advice-lifecycle-success-vs-failure-paths)
   - [Step 3: Pointcut Expressions & Designators](#step-3-pointcut-expressions--designators)
   - [Step 4: Flow Control with `@Around` Advice](#step-4-flow-control-with-around-advice)
   - [Step 5: The "Self-Invocation" Gotcha](#step-5-the-self-invocation-gotcha)
5. [Complete Console Output & Annotated Walkthrough](#complete-console-output--annotated-walkthrough)
6. [Interactive Exercises & Self-Checks](#interactive-exercises--self-checks)
7. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)

---

## What is Aspect-Oriented Programming?

In Object-Oriented Programming (OOP), code is organized into classes and hierarchies. However, some concerns—such as **logging**, **security checks**, **performance profiling**, and **transaction management**—cut across multiple unrelated classes. These are called **cross-cutting concerns**.

Without AOP, cross-cutting logic is duplicated across business methods. With AOP, you define this logic in a single module called an **Aspect**, and the framework automatically weaves it into method execution at runtime via **Dynamic Proxies**.

### Core AOP Terminology

| Term | Definition | Corresponding Repo Example |
| :--- | :--- | :--- |
| **Aspect** | A modular class containing cross-cutting logic | [`TimingAroundAspect`](src/main/java/com/example/aopdemo/aspect/TimingAroundAspect.java) |
| **Join Point** | A point during program execution (in Spring AOP, always a method execution) | Any method in [`ReportService`](src/main/java/com/example/aopdemo/service/ReportService.java) or [`GreetingService`](src/main/java/com/example/aopdemo/service/GreetingService.java) |
| **Pointcut** | A predicate or expression that matches join points | `@Pointcut(...)`, `execution(...)`, `@annotation(...)` |
| **Advice** | Action taken by an aspect at a join point | `@Before`, `@AfterReturning`, `@AfterThrowing`, `@After`, `@Around` |
| **Target Object** | The actual business object being proxied | [`GreetingServiceImpl`](src/main/java/com/example/aopdemo/service/GreetingServiceImpl.java), [`ReportService`](src/main/java/com/example/aopdemo/service/ReportService.java) |
| **AOP Proxy** | An object created by Spring wrapping the target to apply aspects | JDK Dynamic Proxy or CGLIB Proxy |
| **Weaving** | Linking aspects with application objects to create advised proxies | Done at runtime by Spring AOP |

### How Spring AOP Works Under the Hood

```
Caller (TutorialRunner)
       │
       ▼
┌───────────────────────────────────────────────┐
│              Spring AOP Proxy                 │
│                                               │
│  1. Run @Before / @Around pre-logic           │
│  2. Delegate: target.method(...)              │
│  3. Run @AfterReturning / @AfterThrowing      │
│  4. Run @After (finally)                      │
└───────────────────────┬───────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────┐
│             Real Target Bean                  │
│       (GreetingServiceImpl / ReportService)   │
│                                               │
│             [Business Logic]                  │
└───────────────────────────────────────────────┘
```

---

## Project Architecture & File Map

```
src/main/java/com/example/aopdemo/
├── AopDemoApplication.java            # Main app & CommandLineRunner tutorial sequence
├── annotation/
│   ├── Auditable.java                 # Custom annotation holding event metadata
│   ├── LogExecutionTime.java          # Marker annotation for method timing
│   └── RequiresRole.java              # Security annotation declaring required role
├── aspect/
│   ├── AccessControlAspect.java       # @Around advice enforcing role-based authorization
│   ├── AfterLoggingAspect.java        # @After advice (finally block equivalent)
│   ├── AfterReturningLoggingAspect.java # @AfterReturning advice (success only)
│   ├── AfterThrowingLoggingAspect.java  # @AfterThrowing advice (exceptions only)
│   ├── ArgsPointcutAspect.java        # Demonstrates args() pointcut and parameter binding
│   ├── BeforeLoggingAspect.java       # @Before advice (pre-execution)
│   ├── CombinedPointcutAspect.java    # Demonstrates && composition (execution + @annotation)
│   ├── CommonPointcuts.java           # Centralized @Pointcut definitions using ||
│   ├── TimingAroundAspect.java        # @Around advice measuring elapsed execution time
│   └── WithinAspect.java              # Demonstrates within() pointcut matching by class
├── context/
│   └── CurrentUserContext.java        # ThreadLocal context simulating the authenticated user
├── exception/
│   ├── AccessDeniedDemoException.java # Thrown when user lacks required role
│   └── RiskyOperationException.java   # Thrown by ReportService to demonstrate error advice
└── service/
    ├── GreetingService.java           # Interface used to demonstrate JDK Dynamic Proxy
    ├── GreetingServiceImpl.java       # Implementation of GreetingService
    └── ReportService.java             # Class without interface, demonstrating CGLIB & self-invocation
```

---

## Prerequisites & How to Run

### Requirements
- **Java 21** or later (configured as `JAVA_HOME`)
- **Maven 3.8+** (or Maven wrapper)

### Running the Application

In your terminal, navigate to the project directory and run:

```bash
mvn clean spring-boot:run
```

If multiple Java versions are installed on your machine, ensure Java 21+ is active:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21) # macOS
mvn spring-boot:run
```

The application runs the [`TutorialRunner`](src/main/java/com/example/aopdemo/AopDemoApplication.java) upon startup and prints the execution traces directly to the console.

> **Tip on Logging**: Notice in [`src/main/resources/application.yml`](src/main/resources/application.yml) that `logging.pattern.console: "%msg%n"`. This removes date, thread, and logger prefixes so you can inspect pure AOP advice ordering without clutter.

---

## Step-by-Step Learning Guide

Follow this 5-step curriculum to study the code in this repository.

---

### Step 1: Proxy Mechanics (JDK Dynamic Proxy vs. CGLIB)

**Key Files:**
- [`AopDemoApplication.java`](src/main/java/com/example/aopdemo/AopDemoApplication.java) (Lines 43–48)
- [`GreetingService.java`](src/main/java/com/example/aopdemo/service/GreetingService.java) (Interface)
- [`GreetingServiceImpl.java`](src/main/java/com/example/aopdemo/service/GreetingServiceImpl.java) (Implementation)
- [`ReportService.java`](src/main/java/com/example/aopdemo/service/ReportService.java) (Class without interface)
- [`application.yml`](src/main/resources/application.yml)

Spring AOP uses two distinct proxying mechanisms:
1. **JDK Dynamic Proxy**: Standard Java reflection-based proxy (`java.lang.reflect.Proxy`). **Requires an interface**.
2. **CGLIB Proxy**: Bytecode-generated subclass proxy. Generates a subclass of your target class at runtime. **Works for classes without interfaces**.

#### Default Behavior in Spring Boot
By default, Spring Boot sets `spring.aop.proxy-target-class=true`, meaning **CGLIB is used everywhere**, even when a bean implements an interface.

When you run the app by default:
```text
  AopUtils.isJdkDynamicProxy(greetingService) = false
  AopUtils.isCglibProxy(reportService)        = true
```

#### Hands-On Experiment: Enabling JDK Dynamic Proxy
Add this to [`src/main/resources/application.yml`](src/main/resources/application.yml):
```yaml
spring:
  aop:
    proxy-target-class: false
```
Run the application again:
```text
  AopUtils.isJdkDynamicProxy(greetingService) = true
  AopUtils.isCglibProxy(reportService)        = true
```
- Because `greetingService` implements [`GreetingService`](src/main/java/com/example/aopdemo/service/GreetingService.java), Spring now uses a **JDK Dynamic Proxy**.
- Because `reportService` has no interface, Spring automatically falls back to **CGLIB**.

---

### Step 2: The Advice Lifecycle (Success vs. Failure Paths)

**Key Files:**
- [`CommonPointcuts.java`](src/main/java/com/example/aopdemo/aspect/CommonPointcuts.java)
- [`BeforeLoggingAspect.java`](src/main/java/com/example/aopdemo/aspect/BeforeLoggingAspect.java)
- [`AfterReturningLoggingAspect.java`](src/main/java/com/example/aopdemo/aspect/AfterReturningLoggingAspect.java)
- [`AfterThrowingLoggingAspect.java`](src/main/java/com/example/aopdemo/aspect/AfterThrowingLoggingAspect.java)
- [`AfterLoggingAspect.java`](src/main/java/com/example/aopdemo/aspect/AfterLoggingAspect.java)

Spring provides 4 basic advice types (plus `@Around`):

| Advice Annotation | When It Runs | Analogous Java Construct |
| :--- | :--- | :--- |
| `@Before` | Before the target method executes | Code at the start of a method |
| `@AfterReturning` | Only after target completes successfully | Code immediately before `return` |
| `@AfterThrowing` | Only after target throws an unhandled exception | Inside a `catch` block |
| `@After` | Runs in all cases (success or exception) | Inside a `finally` block |

#### 1. Success Path: `greetingService.sayHello("World")`
```text
Success path: sayHello(...)
  [@Before]         -> entering GreetingServiceImpl.sayHello(..) with args [World]
  [business logic] Saying hello to World
  [@AfterReturning]    GreetingServiceImpl.sayHello(..) completed successfully
  [@After]          <- exiting GreetingServiceImpl.sayHello(..) (runs regardless of outcome)
```
- `@Before` executes first, inspecting arguments with `joinPoint.getArgs()`.
- The real target method runs.
- `@AfterReturning` executes because no exception was thrown.
- `@After` executes last as the `finally` cleanup.

#### 2. Failure Path: `reportService.riskyOperation(true)`
```text
Failure path: riskyOperation(true)
  [@Before]         -> entering ReportService.riskyOperation(..) with args [true]
  [within(ReportService)]  call intercepted inside ReportService: ReportService.riskyOperation(..)
  [business logic] Attempting risky operation (shouldFail=true)
  [@AfterThrowing]  x  ReportService.riskyOperation(..) threw: RiskyOperationException: Simulated failure, on purpose
  [@After]          <- exiting ReportService.riskyOperation(..) (runs regardless of outcome)
  [runner] caught expected exception: Simulated failure, on purpose
```
- `@Before` executes.
- Target method throws [`RiskyOperationException`](src/main/java/com/example/aopdemo/exception/RiskyOperationException.java).
- `@AfterReturning` is **skipped**.
- `@AfterThrowing` executes and receives the thrown exception via `throwing = "ex"`.
- `@After` **still executes** (just like `finally`).

---

### Step 3: Pointcut Expressions & Designators

A pointcut defines **where** advice should be applied. This repository demonstrates several Pointcut Designators (PCDs):

#### 1. `execution(...)` — Method Signature Matching
Located in [`CommonPointcuts.java`](src/main/java/com/example/aopdemo/aspect/CommonPointcuts.java):
```java
@Pointcut("execution(* com.example.aopdemo.service.GreetingService.sayHello(..)) || "
        + "execution(* com.example.aopdemo.service.ReportService.riskyOperation(..))")
public void adviceTypeDemoMethods() {}
```
- `*`: Any return type.
- `com.example.aopdemo.service.GreetingService`: Target class or interface.
- `sayHello`: Method name.
- `(..)`: Any number and type of arguments.
- `||`: Combines pointcuts (logical OR).

#### 2. `@annotation(...)` — Annotation-Based Matching
Located in [`TimingAroundAspect.java`](src/main/java/com/example/aopdemo/aspect/TimingAroundAspect.java):
```java
@Around("@annotation(com.example.aopdemo.annotation.LogExecutionTime)")
```
Matches any method annotated with [`@LogExecutionTime`](src/main/java/com/example/aopdemo/annotation/LogExecutionTime.java). This is a best-practice pattern: clean marker annotations keep pointcut expressions simple and decoupled from method names.

#### 3. `within(...)` — Package and Type Matching
Located in [`WithinAspect.java`](src/main/java/com/example/aopdemo/aspect/WithinAspect.java):
```java
@Before("within(com.example.aopdemo.service.ReportService)")
```
Matches any method execution within the `ReportService` class.

#### 4. `args(...)` — Argument Matching & Parameter Binding
Located in [`ArgsPointcutAspect.java`](src/main/java/com/example/aopdemo/aspect/ArgsPointcutAspect.java):
```java
@Before("execution(* com.example.aopdemo.service..*(..)) && args(value)")
public void logIntArgument(int value) {
    log.info("  [args(int)]       matched a call with a single int argument: {}", value);
}
```
Matches any method in `com.example.aopdemo.service` that accepts a single `int` argument, and automatically binds that argument to the `value` parameter of the advice method.

#### 5. Combining Pointcuts (`&&`, `||`, `!`)
Located in [`CombinedPointcutAspect.java`](src/main/java/com/example/aopdemo/aspect/CombinedPointcutAspect.java):
```java
@Before("execution(* com.example.aopdemo.service..*(..)) && @annotation(auditable)")
public void logAuditableEvent(Auditable auditable) {
    log.info("  [execution && @annotation]  audit event recorded: {}", auditable.value());
}
```
Requires the method to be within the service package **AND** have the [`@Auditable`](src/main/java/com/example/aopdemo/annotation/Auditable.java) annotation. It also extracts the annotation instance directly.

---

### Step 4: Flow Control with `@Around` Advice

`@Around` is the most powerful advice type. It completely wraps the target method. It receives a `ProceedingJoinPoint`, allowing the aspect to:
1. Run code before and after the method.
2. Decide **whether or not** to call `proceed()`.
3. Measure execution time.
4. Catch, alter, or suppress exceptions.
5. Modify input arguments or the return value.

#### Pattern A: Performance Profiling
Look at [`TimingAroundAspect.java`](src/main/java/com/example/aopdemo/aspect/TimingAroundAspect.java):
```java
@Around("@annotation(com.example.aopdemo.annotation.LogExecutionTime)")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed(); // proceed to target method
    long elapsed = System.currentTimeMillis() - start;
    log.info("  [@Around/timing]  <- {} took {} ms", joinPoint.getSignature().toShortString(), elapsed);
    return result;
}
```

#### Pattern B: Access Control & Short-Circuiting
Look at [`AccessControlAspect.java`](src/main/java/com/example/aopdemo/aspect/AccessControlAspect.java):
```java
@Around("@annotation(requiresRole)")
public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
    String currentRole = CurrentUserContext.getRole();
    if (!requiresRole.value().equals(currentRole)) {
        log.info("  [@Around/access]  x DENIED - proceed() will NOT be called");
        throw new AccessDeniedDemoException("Role '" + requiresRole.value() + "' required...");
    }
    return joinPoint.proceed();
}
```
When `CurrentUserContext.setRole("USER")` is set, `deleteAllData()` is invoked:
- The advice checks the role.
- Since `"ADMIN"` is required but the current role is `"USER"`, `proceed()` is **never called**.
- The real method never executes, preventing unauthorized actions!
- When `CurrentUserContext.setRole("ADMIN")` is set, `proceed()` is invoked, and the method runs.

---

### Step 5: The "Self-Invocation" Gotcha

**Key Files:**
- [`ReportService.java`](src/main/java/com/example/aopdemo/service/ReportService.java) (Lines 54–60)
- [`AopDemoApplication.java`](src/main/java/com/example/aopdemo/AopDemoApplication.java) (Lines 93–96)

This is one of the most common pitfalls when using Spring AOP.

#### The Problem
In [`ReportService.java`](src/main/java/com/example/aopdemo/service/ReportService.java):
```java
@LogExecutionTime
public void slowOperation() { ... }

public void outerOperation() {
    log.info("  [business logic] outerOperation() starting...");
    this.slowOperation(); // <--- SELF-INVOCATION VIA "this."
    log.info("  [business logic] outerOperation() finished");
}
```

When [`TutorialRunner`](src/main/java/com/example/aopdemo/AopDemoApplication.java) calls `reportService.outerOperation()`:
1. `TutorialRunner` invokes `outerOperation()` on the **Spring Proxy**.
2. The proxy delegates to the real `ReportService` target instance.
3. Inside `outerOperation()`, `this.slowOperation()` calls the method on the **target instance directly**, bypassing the proxy!
4. Because the proxy is bypassed, the `@Around` aspect on `@LogExecutionTime` is **never executed**.

#### Output Proof
```text
SELF-INVOCATION
  [within(ReportService)]  call intercepted inside ReportService: ReportService.outerOperation()
  [business logic] outerOperation() starting - about to call
  [business logic] slowOperation() via "this." (self-invocation)
  [business logic] Doing something slow...
  [business logic] Slow operation finished
  [business logic] outerOperation() finished
No [@Around/timing] lines above the business-logic lines this time -
```
Notice how there is **no `[@Around/timing]` log** for `slowOperation()`!

#### How to Fix Self-Invocation in Real Projects
1. **Refactor**: Move `slowOperation()` into a separate Spring service/component.
2. **Self-Injection**: Inject the proxy of `ReportService` into itself (using `@Lazy` or setter injection) and call `self.slowOperation()`.
3. **`AopContext.currentProxy()`**: Enable `expose-proxy=true` and call `((ReportService) AopContext.currentProxy()).slowOperation()`.
4. **Full AspectJ**: Switch from Spring AOP (dynamic proxies) to AspectJ with Compile-Time (CTW) or Load-Time Weaving (LTW), which weaves bytecode directly into class files.

---

## Complete Console Output & Annotated Walkthrough

Below is the complete output from running the tutorial, annotated with what each line demonstrates:

```text
  AopUtils.isJdkDynamicProxy(greetingService) = false  <-- Default Spring Boot behavior (CGLIB)
  AopUtils.isCglibProxy(reportService)        = true   <-- ReportService has no interface, uses CGLIB

Success path: sayHello(...)
  [@Before]         -> entering GreetingServiceImpl.sayHello(..) with args [World]
  [business logic] Saying hello to World               <-- Target method executed
  [@AfterReturning]    GreetingServiceImpl.sayHello(..) completed successfully
  [@After]          <- exiting GreetingServiceImpl.sayHello(..) (runs regardless of outcome)

Failure path: riskyOperation(true)
  [@Before]         -> entering ReportService.riskyOperation(..) with args [true]
  [within(ReportService)]  call intercepted inside ReportService: ReportService.riskyOperation(..)
  [business logic] Attempting risky operation (shouldFail=true)  <-- Target throws exception
  [@AfterThrowing]  x  ReportService.riskyOperation(..) threw: RiskyOperationException: Simulated failure, on purpose
  [@After]          <- exiting ReportService.riskyOperation(..) (runs regardless of outcome)
  [runner] caught expected exception: Simulated failure, on purpose

@Around + @annotation()
  [@Around/timing]  -> about to call proceed() for ReportService.slowOperation()
  [within(ReportService)]  call intercepted inside ReportService: ReportService.slowOperation()
  [business logic] Doing something slow...
  [business logic] Slow operation finished
  [@Around/timing]  <- ReportService.slowOperation() took 306 ms  <-- Elapsed time measured

Access-Control Aspect
USER role
  [@Around/access]  checking ReportService.deleteAllData() - required=ADMIN, current=USER
  [@Around/access]  x DENIED - proceed() will NOT be called       <-- Method execution aborted
  [runner] caught expected exception: Role 'ADMIN' required, but current role is 'USER'

ADMIN role
  [@Around/access]  checking ReportService.deleteAllData() - required=ADMIN, current=ADMIN
  [@Around/access]  ALLOWED - calling proceed()                  <-- Access granted
  [within(ReportService)]  call intercepted inside ReportService: ReportService.deleteAllData()
  [business logic] ALL DATA DELETED (not really - this is a demo)

Pointcut Expressions
  [args(int)]       matched a call with a single int argument: 42 <-- Extracted argument
  [within(ReportService)]  call intercepted inside ReportService: ReportService.processNumber(..)
  [business logic] Processing number 42

COMBINING POINTCUTS
  [execution && @annotation]  audit event recorded: REPORT_GENERATED  <-- Combined PCDs matched
  [within(ReportService)]  call intercepted inside ReportService: ReportService.auditedAction()
  [business logic] Performing an audited action

SELF-INVOCATION
  [within(ReportService)]  call intercepted inside ReportService: ReportService.outerOperation()
  [business logic] outerOperation() starting - about to call
  [business logic] slowOperation() via "this." (self-invocation)
  [business logic] Doing something slow...
  [business logic] Slow operation finished
  [business logic] outerOperation() finished
No [@Around/timing] lines above the business-logic lines this time -  <-- Self-invocation skipped proxy!

within()
  [within(ReportService)]  call intercepted inside ReportService: ReportService.generateReport(..)
  [business logic] Generating report of type QUARTERLY
  Done
```

---

## Interactive Exercises & Self-Checks

To solidify your understanding, try these exercises:

### Exercise 1: Toggle JDK Dynamic Proxy
In [`src/main/resources/application.yml`](src/main/resources/application.yml), add:
```yaml
spring:
  aop:
    proxy-target-class: false
```
Run `mvn spring-boot:run` and observe:
1. What does `AopUtils.isJdkDynamicProxy(greetingService)` print?
2. Look at the `sayHello` log lines: why does the signature say `GreetingService.sayHello(..)` instead of `GreetingServiceImpl.sayHello(..)`?

### Exercise 2: Capture and Inspect Return Values
Open [`AfterReturningLoggingAspect.java`](src/main/java/com/example/aopdemo/aspect/AfterReturningLoggingAspect.java). Modify the annotation to capture the return value:
```java
@AfterReturning(pointcut = "com.example.aopdemo.aspect.CommonPointcuts.adviceTypeDemoMethods()", returning = "result")
public void logAfterReturning(JoinPoint joinPoint, Object result) {
    log.info("  [@AfterReturning]    {} returned: {}", joinPoint.getSignature().toShortString(), result);
}
```
Run the application and verify that `Hello, World` is logged.

### Exercise 3: Fix Self-Invocation with Self-Injection
In [`ReportService.java`](src/main/java/com/example/aopdemo/service/ReportService.java):
1. Add `@Autowired @Lazy private ReportService self;`
2. Change `this.slowOperation();` in `outerOperation()` to `self.slowOperation();`.
3. Run the application and check if `[@Around/timing]` now appears for `slowOperation()`.

### Exercise 4: Build a Retry Aspect
1. Create an annotation `@Retryable(maxAttempts = 3)`.
2. Create a `@Around("@annotation(retryable)")` aspect.
3. Wrap `joinPoint.proceed()` in a `while` loop with a `try-catch` to retry [`riskyOperation`](src/main/java/com/example/aopdemo/service/ReportService.java) when it throws an exception.

---

## Quick Reference Cheat Sheet

### Advice Types

| Advice | Signature | Can Prevent Execution? | Can Alter Return Value? |
| :--- | :--- | :---: | :---: |
| `@Before` | `void advice(JoinPoint jp)` | Only by throwing exception | No |
| `@AfterReturning` | `void advice(JoinPoint jp, Object result)` | Only by throwing exception | No |
| `@AfterThrowing` | `void advice(JoinPoint jp, Throwable ex)` | No (exception rethrown) | No |
| `@After` | `void advice(JoinPoint jp)` | Only by throwing exception | No |
| `@Around` | `Object advice(ProceedingJoinPoint pjp) throws Throwable` | **Yes** (omit `proceed()`) | **Yes** (return different value) |

### Common Pointcut Designators (PCDs)

| Designator | Description | Example |
| :--- | :--- | :--- |
| `execution(...)` | Matches method signatures | `execution(public * com.example.service.*.*(..))` |
| `within(...)` | Matches all methods within specific types | `within(com.example.service..*)` |
| `@annotation(...)` | Matches methods with a specific annotation | `@annotation(com.example.annotation.LogExecutionTime)` |
| `@within(...)` | Matches methods of classes with a specific annotation | `@within(org.springframework.stereotype.Service)` |
| `args(...)` | Matches methods whose runtime arguments match types | `args(java.lang.String, ..)` |
| `@args(...)` | Matches methods whose argument classes have an annotation | `@args(com.example.annotation.ValidEntity)` |
| `bean(...)` | Matches methods on Spring beans by bean name | `bean(*Service)` |

### Logic Operators
- `&&`: Both pointcuts must match (AND)
- `||`: Either pointcut must match (OR)
- `!`: Negation (NOT)
