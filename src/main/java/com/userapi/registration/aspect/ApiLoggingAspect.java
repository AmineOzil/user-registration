package com.userapi.registration.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP aspect for logging API calls at the controller boundary.
 * Logs HTTP method, URI, status code, duration, and optionally request/response details.
 */
@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    // Flag to prevent duplicate logging in service layer during HTTP calls
    static final ThreadLocal<Boolean> IN_HTTP_CALL = ThreadLocal.withInitial(() -> false);

    @Pointcut("execution(* com.userapi.registration.controller..*(..))")
    public void controllerLayer() {}

    /**
     * Logs API call execution.
     */
    @Around("controllerLayer()")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        IN_HTTP_CALL.set(true);

        String controller = joinPoint.getTarget().getClass().getSimpleName();
        String method = joinPoint.getSignature().getName();
        String signature = controller + "." + method;

        HttpServletRequest request = getCurrentRequest();
        String httpInfo = request != null 
                ? request.getMethod() + " " + request.getRequestURI() 
                : "N/A";

        long startNanos = System.nanoTime();

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("ApiCall START {} http={} args={}", 
                        signature, httpInfo, SafeLog.toSafeArgs(joinPoint.getArgs()));
            }

            Object result = joinPoint.proceed();

            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            int status = extractHttpStatus(result);

            if (logger.isDebugEnabled()) {
                Object responseBody = extractResponseBody(result);
                logger.debug("ApiCall SUCCESS {} http={} status={} duration={}ms result={}", 
                        signature, httpInfo, status, durationMs, SafeLog.toSafeValue(responseBody));
            } else {
                logger.info("ApiCall SUCCESS {} http={} status={} duration={}ms", 
                        signature, httpInfo, status, durationMs);
            }

            return result;

        } catch (Throwable ex) {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            
            // Log only exception type and duration (stacktrace handled by GlobalExceptionHandler)
            logger.info("ApiCall FAIL {} http={} duration={}ms exception={}", 
                    signature, httpInfo, durationMs, ex.getClass().getSimpleName());
            
            throw ex;
        } finally {
            IN_HTTP_CALL.remove();
        }
    }

    private HttpServletRequest getCurrentRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest();
        }
        return null;
    }

    private int extractHttpStatus(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }
        return 200;
    }

    private Object extractResponseBody(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }
        return result;
    }
}