package com.userapi.registration.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logs service layer execution for non-HTTP contexts (batch, scheduler, direct calls).
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.userapi.registration.service.*.*(..))")
    public void serviceLayer() {}

    /**
     * Logs service layer execution.
     */
    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Skip logging if already in HTTP call context (ApiLoggingAspect handles it)
        if (Boolean.TRUE.equals(ApiLoggingAspect.IN_HTTP_CALL.get())) {
            return joinPoint.proceed();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String signature = className + "." + methodName;

        long startTime = System.nanoTime();

        try {
            if (logger.isDebugEnabled()) {
                Object[] args = joinPoint.getArgs();
                String safeArgs = SafeLog.toSafeArgs(args);
                logger.debug("ServiceCall START {} args={}", signature, safeArgs);
            }

            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;

            if (logger.isDebugEnabled()) {
                String safeResult = SafeLog.toSafeValue(result);
                logger.debug("ServiceCall SUCCESS {} duration={}ms result={}", signature, durationMs, safeResult);
            } else {
                logger.info("ServiceCall SUCCESS {} duration={}ms", signature, durationMs);
            }

            return result;
        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.warn("ServiceCall FAIL {} duration={}ms exception={}", 
                    signature, durationMs, ex.getClass().getSimpleName());
            throw ex;
        }
    }
}