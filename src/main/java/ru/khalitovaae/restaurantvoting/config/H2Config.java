package ru.khalitovaae.restaurantvoting.config;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@EnableCaching
public class H2Config {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.port}")
    private String h2TcpPort;

    // TODO fix. error when run all tests
    // https://stackoverflow.com/questions/46236808/spring-junit-org-h2-jdbc-jdbcsqlexception-exception-opening-port-9092-port-m
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("default")
    public Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort);
    }
}
