package uk.gov.hmcts.reform.feature.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // TODO replace with proper set up from configs and azure 'n' stuff
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User
            .withUsername("user")
            .password("password")
            .roles("USER")
            .build()
        );
        manager.createUser(User
            .withUsername("master")
            .password("password")
            .roles("USER", "EDITOR")
            .build()
        );
        manager.createUser(User
            .withUsername("admin")
            .password("admin")
            .roles("USER", "EDITOR", "ADMIN")
            .build()
        );

        return manager;
    }

    @Configuration
    @Order(1)
    public static class Ff4jWebConsoleSecurityAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/ff4j-web-console/**")
                .authorizeRequests()
                .anyRequest().hasRole("ADMIN")
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
                .antMatchers(HttpMethod.DELETE).hasRole("EDITOR")
                .antMatchers(HttpMethod.POST).hasRole("EDITOR")
                .antMatchers(HttpMethod.PUT).hasRole("EDITOR")
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
                .csrf().disable();
        }
    }
}
