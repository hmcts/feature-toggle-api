package uk.gov.hmcts.reform.feature.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.gov.hmcts.reform.feature.webconsole.WebconsoleUserConfig;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                .exceptionHandling().authenticationEntryPoint(new EntryPoint())
                .and()
                .csrf().disable();
        }

        private static class EntryPoint implements AuthenticationEntryPoint {

            static final Logger log = LoggerFactory.getLogger(EntryPoint.class);

            @Override
            public void commence(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthenticationException authException) throws IOException, ServletException {
                log.warn(authException.getMessage(), authException);

                response.sendRedirect(response.encodeRedirectURL("/login?accessDenied"));
            }
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
                .formLogin().successHandler(new SuccessHandler()).permitAll()
                .and()
                .logout().logoutSuccessUrl("/?logout").permitAll()
                .and()
                .csrf().disable();
        }

        private static class SuccessHandler implements AuthenticationSuccessHandler {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                boolean isAdmin = authentication.getAuthorities().stream().anyMatch(authority ->
                    authority.getAuthority().equals("ROLE_ADMIN")
                );

                if (isAdmin) {
                    response.sendRedirect(response.encodeRedirectURL("/ff4j-web-console/home"));
                } else {
                    response.sendRedirect(response.encodeRedirectURL("/?login"));
                }
            }
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
