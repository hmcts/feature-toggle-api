package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.gov.hmcts.reform.feature.security.AuthExceptionEntryPoint;
import uk.gov.hmcts.reform.feature.security.LoginSuccessHandler;
import uk.gov.hmcts.reform.feature.security.UserDetailsConfigurer;

import javax.sql.DataSource;

import static uk.gov.hmcts.reform.feature.security.Roles.ROLE_ADMIN;
import static uk.gov.hmcts.reform.feature.security.Roles.ROLE_EDITOR;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${flyway.enabled:true}")
    private boolean flywayEnabled;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

        UserDetailsManagerConfigurer<?, ?> configurer;

        if (flywayEnabled) {
            configurer = auth.jdbcAuthentication().dataSource(dataSource);
        } else {
            configurer = auth.inMemoryAuthentication();
        }

        new UserDetailsConfigurer(configurer, passwordEncoder).configure();
    }

    @Configuration
    @Order(1)
    public static class Ff4jWebConsoleSecurityAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/ff4j-web-console/**")
                .authorizeRequests()
                .anyRequest().hasRole(ROLE_ADMIN)
                .and()
                .exceptionHandling().authenticationEntryPoint(new AuthExceptionEntryPoint())
                .and()
                .csrf().disable();
        }
    }

    @Configuration
    @Order(2)
    public static class ApiSecurityAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/api/ff4j/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.DELETE).hasRole(ROLE_EDITOR)
                .antMatchers(HttpMethod.POST).hasRole(ROLE_EDITOR)
                .antMatchers(HttpMethod.PUT).hasRole(ROLE_EDITOR)
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
        }
    }

    @Configuration
    public static class DefaultSecurityAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/", "/health", "/info", "/v2/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(new LoginSuccessHandler()).permitAll()
                .and()
                .logout().logoutSuccessUrl("/?logout").permitAll()
                .and()
                .csrf().disable();
        }
    }
}
