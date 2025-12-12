package com.example.devicesapi.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example.devicesapi.services.*.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        log.info("Service Method Started: {}", joinPoint.getSignature().getName());
        log.info("- Arguments: {}", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void afterServiceMethod(JoinPoint joinPoint, Object result) {
        log.info("Service Method {} returned {}",
            joinPoint.getSignature().getName(),
            result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void onException(JoinPoint joinPoint, Throwable exception) {
        log.info("Exception in Method {} : {}",
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

}

