package com.mts.backend.infrastructure.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
public class LoggingConfig {

    private static final String LOG_SEPARATOR = "=".repeat(50);

    @Around("execution(* com.mts.backend.application..*.*(..))")
    public Object logAroundApplicationLayerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        String parameters = getParametersAsString(joinPoint, signature);

        logger.info("{}\n▶ START: {}.{}({})", LOG_SEPARATOR, className, methodName, parameters);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("✓ SUCCESS: {}.{} - completed in {} ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("✗ ERROR: {}.{} - failed after {} ms - {}: {}",
                    className, methodName, executionTime,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } finally {
            logger.info("◼ END: {}.{}\n{}", className, methodName, LOG_SEPARATOR);
        }
    }

    private String getParametersAsString(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames == null || parameterNames.length == 0) {
            return "";
        }

        return IntStream.range(0, parameterNames.length)
                .mapToObj(i -> parameterNames[i] + "=" + (args[i] != null ?
                        (args[i].toString().length() > 100 ?
                                args[i].toString().substring(0, 100) + "..." :
                                args[i].toString()) :
                        "null"))
                .collect(Collectors.joining(", "));
    }

    /**
     * Log around api layer methods
     */
    @Around("execution(* com.mts.backend.api..*.*(..))")
    public Object logAroundApiLayerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        String parameters = getParametersAsString(joinPoint, signature);

        logger.info("{}\n▶ START: {}.{}({})", LOG_SEPARATOR, className, methodName, parameters);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("✓ SUCCESS: {}.{} - completed in {} ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("✗ ERROR: {}.{} - failed after {} ms - {}: {}",
                    className, methodName, executionTime,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } finally {
            logger.info("◼ END: {}.{}\n{}", className, methodName, LOG_SEPARATOR);
        }
    }
}