package ru.khalitovaae.restaurantvoting.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.Clock;
import java.util.Locale;


@Configuration
@EnableCaching
public class Config {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(5);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    protected Module module() {
        return new Hibernate5Module();
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver =
                new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(new Locale("ru"));
        return cookieLocaleResolver;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }
}
