package ru.khalitovaae.restaurantvoting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.khalitovaae.restaurantvoting.web.formatter.LocalDateFormatter;
import ru.khalitovaae.restaurantvoting.web.formatter.LocalTimeFormatter;
import ru.khalitovaae.restaurantvoting.web.json.JacksonObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FormattingConversionServiceFactoryBean formattingConversionServiceFactoryBean() {
        FormattingConversionServiceFactoryBean formattingBean =
                new FormattingConversionServiceFactoryBean();
        Set<Formatter> formatters = new HashSet<>();
        formatters.add(new LocalDateFormatter());
        formatters.add(new LocalTimeFormatter());
        formattingBean.setFormatters(formatters);
        return formattingBean;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
        messageConverters.add(getHttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authenticationPrincipalArgumentResolver());
    }

    private StringHttpMessageConverter getHttpMessageConverter() {
        StringHttpMessageConverter httpMessageConverter = new StringHttpMessageConverter();
        MediaType mediaTypeText = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        MediaType mediaTypeHtml = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);
        httpMessageConverter.setSupportedMediaTypes(List.of(mediaTypeText, mediaTypeHtml));
        return httpMessageConverter;
    }
}