package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import uk.gov.hmcts.reform.feature.authorisation.IdamUserAuthoriser;
import uk.gov.hmcts.reform.feature.authorisation.IdamUserAuthoriserClient;

import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore({SecurityConfiguration.class})
public class AuthenticationProviderConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    @ConditionalOnProperty(prefix = "flyway", name = "enabled", matchIfMissing = true)
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl jdbcImpl = new JdbcDaoImpl();

        jdbcImpl.setDataSource(dataSource);
        jdbcImpl.setUsersByUsernameQuery("select username, password, enabled"
            + " from users where username=?");
        jdbcImpl.setAuthoritiesByUsernameQuery("select username, authority "
            + "from authorities where username=?");
        return jdbcImpl;
    }

    @Bean
    @ConditionalOnProperty(prefix = "flyway", name = "enabled", havingValue = "false")
    public UserDetailsService inMemoryUserDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    @ConditionalOnProperty(prefix = "idam", name = "url")
    public IdamUserAuthoriser idamUserAuthoriser(@Value("${idam.url}") String idamUrl) {
        return new IdamUserAuthoriserClient(idamUrl);
    }

    @Bean
    @ConditionalOnProperty(prefix = "idam", name = "url", havingValue = "false", matchIfMissing = true)
    public IdamUserAuthoriser idamUserAuthoriserStub() {
        return (String authHeader) -> null;
    }

    @Bean
    public PasswordEncoder passwordencoder() {
        return new BCryptPasswordEncoder();
    }
}
