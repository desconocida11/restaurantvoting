package ru.khalitovaae.restaurantvoting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khalitovaae.restaurantvoting.service.UserService;
import ru.khalitovaae.restaurantvoting.web.json.JsonUtil;

import javax.annotation.PostConstruct;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final String[] swaggerPatterns = {"/swagger-ui.html", "/swagger-ui/**",
            "/swagger-resources/**", "/v3/api-docs/**"};

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

//    <security:global-method-security secured-annotations="enabled" pre-post-annotations="enabled"/>

    @Bean
    public PasswordEncoder passwordEncoder() {
        return createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/profile/register").anonymous()
                .antMatchers("/login").anonymous()
                .antMatchers("/admin", "/admin/**").hasRole(ROLE_ADMIN)
                .antMatchers("/profile").hasRole(ROLE_USER)
                .antMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/restaurants/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT, "/restaurants/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT, "/votes/**").hasRole(ROLE_USER)
                .antMatchers(HttpMethod.POST, "/votes/**").hasRole(ROLE_USER)
                .antMatchers(HttpMethod.DELETE, "/votes/**").hasRole(ROLE_USER)
                .antMatchers(swaggerPatterns).permitAll()
                .antMatchers("/**").authenticated()
                .and().httpBasic();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/resources/**");
    }

}
