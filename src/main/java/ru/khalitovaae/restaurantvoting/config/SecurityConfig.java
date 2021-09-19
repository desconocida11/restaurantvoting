package ru.khalitovaae.restaurantvoting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khalitovaae.restaurantvoting.model.Role;
import ru.khalitovaae.restaurantvoting.service.UserService;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/profile/register").anonymous()
                .antMatchers("/login").anonymous()
                .antMatchers("/admin", "/admin/**").hasRole(Role.ADMIN.name())
                .antMatchers("/profile").hasRole(Role.USER.name())
                .antMatchers(HttpMethod.GET, "/menus/**").permitAll()
                .antMatchers(HttpMethod.POST, "/menus/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/menus/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/menus/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/restaurants/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/restaurants/**").hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/votes/**").hasRole(Role.USER.name())
                .antMatchers(HttpMethod.POST, "/votes/**").hasRole(Role.USER.name())
                .antMatchers(HttpMethod.DELETE, "/votes/**").hasRole(Role.USER.name())
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
