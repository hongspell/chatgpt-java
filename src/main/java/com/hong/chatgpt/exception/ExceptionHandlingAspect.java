package com.hong.chatgpt.exception;

import com.hong.chatgpt.utils.OpenAIResultCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
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
            OpenAIResultCode openAIError = OpenAIResultCode.fromStatusCode(e.getStatusCode().value());
            if (openAIError != null) {
                throw new CommonException(openAIError.getStatusCode(), openAIError.getMessage());
            } else {
                throw new CommonException(500, "Unknown Error: " + e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            System.out.println("Caught exception of type: " + e.getClass().getName());
            // 处理其他异常
            throw new CommonException(500, "System Error: " + e.getMessage());
        }
    }

}
