package com.hong.chatgpt.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @Author hong
 * @Description Exception Aspect
 * @Date
 **/
@Aspect
@Component
public class ExceptionHandlingAspect {

    @Around("execution(* com.hong.chatgpt..*(..))")
    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around advice is running for method: " + joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } catch (WebClientResponseException e) {
            OpenAIError openAIError = OpenAIError.fromStatusCode(e.getStatusCode().value());
            if (openAIError != null) {
                throw new CommonException(openAIError.getOverview(), openAIError.getStatusCode(), openAIError.getSolution());
            } else {
                throw new CommonException("Unknown Error: " + e.getResponseBodyAsString(), 500, "Please contact supports.");
            }
        } catch (Exception e) {
            System.out.println("Caught exception of type: " + e.getClass().getName());
            // 处理其他异常
            throw new CommonException("System Error: " + e.getMessage(), 500, "Please contact supports.");
        }
    }

}
