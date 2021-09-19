package ru.khalitovaae.restaurantvoting.util.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.khalitovaae.restaurantvoting.AuthorizedUser;
import ru.khalitovaae.restaurantvoting.util.SecurityUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Aspect
public class LoggingAspect {

    @Before(value = "execution(public * ru.khalitovaae.restaurantvoting.web.*.*(..))")
    public void beforeRestaurantController(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Logger log = LoggerFactory.getLogger(signature.getMethod().getDeclaringClass());
        log.info("method: {}", signature.getName());
        AuthorizedUser authUser = SecurityUtil.safeGet();
        log.info("auth user: {}", authUser != null ? authUser.getId() : "anonymous");
        if (signature.getParameterNames().length > 0) {
            String args = String.join(", ", signature.getParameterNames());
            log.info("args names: {}", args);
        }
        if (joinPoint.getArgs().length > 0) {
            String values = Arrays.stream(joinPoint.getArgs())
                    .map(o -> o == null ? "null" : o.toString())
                    .collect(Collectors.joining(", "));
            log.info("values names: {}", values);
        }
    }
}
