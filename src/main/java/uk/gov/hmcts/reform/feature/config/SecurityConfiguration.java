package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.gov.hmcts.reform.feature.webconsole.WebconsoleUserConfig;

import java.util.List;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(WebconsoleUserConfig.class)
@EnableWebSecurity
public class SecurityConfiguration {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private WebconsoleUserConfig userConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcConfigurer =
            auth.jdbcAuthentication().dataSource(dataSource);

        //Create admin users
        configureUsers(userConfig.getUsers().getAdmins(), jdbcConfigurer, ROLE_ADMIN, ROLE_EDITOR);

        //Create editor users
        configureUsers(userConfig.getUsers().getEditors(), jdbcConfigurer, ROLE_EDITOR);

        //Create read only users
        configureUsers(userConfig.getUsers().getReaders(), jdbcConfigurer, ROLE_USER);
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
                .httpBasic()
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
                .antMatchers("/", "/health", "/info").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
        }
    }

    private void configureUsers(
        List<WebconsoleUserConfig.UserDetails> userDetails,
        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcConfigurer,
        String... roles
    ) {
        userDetails.stream()
            .filter(user -> !jdbcConfigurer.getUserDetailsService().userExists(user.getUsername()))
            .forEach(user -> jdbcConfigurer
                .withUser(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(roles)
            );
    }
}
