package uk.gov.hmcts.reform.feature.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/health", "/info").permitAll()
            .antMatchers(HttpMethod.GET, "/api/ff4j/**").hasRole("USER")
            .antMatchers(HttpMethod.OPTIONS, "/api/ff4j/**").hasRole("USER")
            .antMatchers(HttpMethod.DELETE, "api/ff4j/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "api/ff4j/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "api/ff4j/**").hasRole("ADMIN")
            .antMatchers("/ff4j-web-console/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable();
    }

    // TODO replace with proper set up from configs and azure 'n' stuff
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("password")
            .roles("USER")
            .and()
            .withUser("admin")
            .password("admin")
            .roles("USER", "ADMIN");
    }
}
