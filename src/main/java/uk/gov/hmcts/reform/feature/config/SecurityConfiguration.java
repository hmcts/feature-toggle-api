package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import uk.gov.hmcts.reform.feature.security.AuthExceptionEntryPoint;
import uk.gov.hmcts.reform.feature.security.CustomAccessDeniedHandler;
import uk.gov.hmcts.reform.feature.security.CustomUserPermissionsFilter;
import uk.gov.hmcts.reform.feature.security.LoginSuccessHandler;
import uk.gov.hmcts.reform.feature.security.UserDetailsConfigurer;

import javax.sql.DataSource;

import static uk.gov.hmcts.reform.feature.security.Roles.ADMIN;
import static uk.gov.hmcts.reform.feature.security.Roles.EDITOR;

@Configuration
@EnableConfigurationProperties(UserConfigurationProperties.class)
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserConfigurationProperties userConfig;

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

        new UserDetailsConfigurer(configurer, passwordEncoder).configure(userConfig);
    }

    @Configuration
    @Order(1)
    public static class Ff4jWebConsoleSecurityAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/ff4j-web-console/**")
                .authorizeRequests()
                .anyRequest().hasRole(ADMIN)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new AuthExceptionEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
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
                .antMatchers(HttpMethod.DELETE).hasRole(EDITOR)
                .antMatchers(HttpMethod.POST).hasRole(EDITOR)
                .antMatchers(HttpMethod.PUT).hasRole(EDITOR)
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                // https://docs.spring.io/spring-security/site/docs/current/reference/html/security-filter-chain.html#filter-ordering
                // sticking custom filter to the end of the chain
                .addFilterAfter(new CustomUserPermissionsFilter("/api/ff4j/check/**"), FilterSecurityInterceptor.class)
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
