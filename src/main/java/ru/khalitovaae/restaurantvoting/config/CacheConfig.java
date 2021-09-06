package ru.khalitovaae.restaurantvoting.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

//    <!-- enables scanning for @Cacheable annotation -->
//    <cache:annotation-driven cache-manager="ehCacheManager"/>
//
//    <!--https://imhoratiu.wordpress.com/2017/01/26/spring-4-with-ehcache-3-how-to/-->
//    <bean id="ehCacheManager" class="org.springframework.cache.jcache.JCacheCacheManager">
//        <property name="cacheManager">
//            <bean class="org.springframework.cache.jcache.JCacheManagerFactoryBean" p:cacheManagerUri="classpath:cache/ehcache.xml"/>
//        </property>
//    </bean>


}
