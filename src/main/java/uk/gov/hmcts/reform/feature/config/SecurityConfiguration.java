package uk.gov.hmcts.reform.feature.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import uk.gov.hmcts.reform.feature.webconsole.Ff4jUsersConfig;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(Ff4jUsersConfig.class)
@EnableWebSecurity
@ConditionalOnProperty(prefix = "flyway", name = "enabled", matchIfMissing = true)
public class SecurityConfiguration {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Ff4jUsersConfig userConfig;

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
        configureUsers(userConfig.getAdmins(), jdbcConfigurer, ROLE_ADMIN, ROLE_EDITOR);

        //Create editor users
        configureUsers(userConfig.getEditors(), jdbcConfigurer, ROLE_EDITOR);

        //Create read only users
        configureUsers(userConfig.getReaders(), jdbcConfigurer, ROLE_USER);
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
                    // since roles are created with automatic prefix of `ROLE_` - authorities come in raw
                    // need to strip the prefix to match successfully
                    authority.getAuthority().replaceFirst("ROLE_", "").equals(ROLE_ADMIN)
                );
                String targetUrl = isAdmin ? "/ff4j-web-console/home" : "/?login";

                response.sendRedirect(response.encodeRedirectURL(targetUrl));
            }
        }
    }

    private void configureUsers(
        List<Ff4jUsersConfig.UserDetails> userDetails,
        JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcConfigurer,
        String... roles
    ) {
        userDetails.stream()
            .forEach(user -> {
                final String username = user.getUsername();
                final String password = user.getPassword();

                JdbcUserDetailsManager userDetailsService = jdbcConfigurer.getUserDetailsService();

                if (userDetailsService.userExists(username)) {
                    //This will delete authorities and then user
                    userDetailsService.deleteUser(username);
                }

                jdbcConfigurer
                    .withUser(username)
                    .password(passwordEncoder.encode(password))
                    .roles(roles);
            });
    }
}
