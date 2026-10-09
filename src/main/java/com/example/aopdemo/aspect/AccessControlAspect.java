package com.example.aopdemo.aspect;

import com.example.aopdemo.annotation.RequiresRole;
import com.example.aopdemo.context.CurrentUserContext;
import com.example.aopdemo.exception.AccessDeniedDemoException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AccessControlAspect {

    @Around("@annotation(requiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        String currentRole = CurrentUserContext.getRole();

        log.info("  [@Around/access]  checking {} - required={}, current={}",
                method, requiresRole.value(), currentRole);

        if (!requiresRole.value().equals(currentRole)) {
            log.info("  [@Around/access]  x DENIED - proceed() will NOT be called");
            throw new AccessDeniedDemoException(
                    "Role '" + requiresRole.value() + "' required, but current role is '" + currentRole + "'");
        }

        log.info("  [@Around/access]  ALLOWED - calling proceed()");
        return joinPoint.proceed();
    }
}
