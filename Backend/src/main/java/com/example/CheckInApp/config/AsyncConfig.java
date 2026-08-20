package com.example.CheckInApp.config;

import com.example.CheckInApp.exception.EmailDeliveryException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            if (ex instanceof EmailDeliveryException ede) {
                throw new RuntimeException("Email delivery failed for: " + ede.getFailedRecipients(), ede);
            } else {
                throw new RuntimeException("Async error in " + method.getName(), ex);
            }
        };
    }
}
