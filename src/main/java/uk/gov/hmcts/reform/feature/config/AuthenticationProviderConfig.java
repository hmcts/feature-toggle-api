package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore({SecurityConfiguration.class})
@DependsOn({"flyway", "flywayInitializer"})
public class AuthenticationProviderConfig {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl jdbcImpl = new JdbcDaoImpl();

        jdbcImpl.setDataSource(dataSource);
        jdbcImpl.setUsersByUsernameQuery("select username, password, enabled"
            + " from users where username=?");
        jdbcImpl.setAuthoritiesByUsernameQuery("select username, authority "
            + "from authorities where username=?");
        return jdbcImpl;
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordencoder() {
        return new BCryptPasswordEncoder();
    }
}
