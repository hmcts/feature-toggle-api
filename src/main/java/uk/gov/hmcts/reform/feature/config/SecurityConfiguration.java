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

import static org.springframework.security.core.userdetails.User.UserBuilder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // TODO replace with proper set up from configs and azure 'n' stuff
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(getUserBuilder("user", "password")
            .roles("USER")
            .authorities("READ")
            .build()
        );
        manager.createUser(getUserBuilder("admin", "admin")
            .roles("USER", "ADMIN")
            .authorities("READ", "WRITE")
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
                .anyRequest().hasRole("USER")
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
                .antMatchers(HttpMethod.GET).hasAuthority("READ")
                .antMatchers(HttpMethod.OPTIONS).hasAuthority("READ")
                .antMatchers(HttpMethod.DELETE).hasAuthority("WRITE")
                .antMatchers(HttpMethod.POST).hasAuthority("WRITE")
                .antMatchers(HttpMethod.PUT).hasAuthority("WRITE")
                .anyRequest().hasRole("USER")
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

    private UserBuilder getUserBuilder(String username, String password) {
        return User.withUsername(username).password(password);
    }
}
