package ru.khalitovaae.restaurantvoting.util.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.khalitovaae.restaurantvoting.AuthorizedUser;
import ru.khalitovaae.restaurantvoting.web.RestaurantController;
import ru.khalitovaae.restaurantvoting.util.SecurityUtil;

import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect {

    @Before(value = "execution(public * ru.khalitovaae.restaurantvoting.web.*.*(..))")
    public void beforeRestaurantController(JoinPoint joinPoint) {
        final Logger log = LoggerFactory.getLogger(RestaurantController.class);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("method: {}", signature.getName());
        AuthorizedUser authUser = SecurityUtil.safeGet();
        log.info("auth user: {}", authUser != null ? authUser.getId() : "anonymous");
        Arrays.stream(signature.getParameterNames()).forEach(s -> log.info("arg name: {}", s));
        Arrays.stream(joinPoint.getArgs()).forEach(o -> log.info("arg value: {}", o));
    }
}
