package uk.gov.hmcts.reform.feature.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import uk.gov.hmcts.reform.feature.webconsole.Ff4jUsersConfig;
import uk.gov.hmcts.reform.feature.webconsole.Ff4jUsersConfig.UserDetails;

import java.util.List;

import static uk.gov.hmcts.reform.feature.security.Roles.ROLE_ADMIN;
import static uk.gov.hmcts.reform.feature.security.Roles.ROLE_EDITOR;
import static uk.gov.hmcts.reform.feature.security.Roles.ROLE_USER;

@EnableConfigurationProperties(Ff4jUsersConfig.class)
public class UserDetailsConfigurer {

    private final UserDetailsManagerConfigurer<?, ?> configurer;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private Ff4jUsersConfig userConfig;

    public UserDetailsConfigurer(UserDetailsManagerConfigurer<?, ?> configurer, PasswordEncoder passwordEncoder) {
        this.configurer = configurer;
        this.passwordEncoder = passwordEncoder;
    }

    public void configure() {
        configureUsers(userConfig.getAdmins(), ROLE_ADMIN, ROLE_EDITOR);
        configureUsers(userConfig.getEditors(), ROLE_EDITOR);
        configureUsers(userConfig.getReaders(), ROLE_USER);
    }

    private void configureUsers(List<UserDetails> userDetails, String... roles) {
        userDetails.forEach(user -> {
            final String username = user.getUsername();
            final String password = user.getPassword();

            UserDetailsManager userDetailsService = configurer.getUserDetailsService();

            if (userDetailsService.userExists(username)) {
                //This will delete authorities and then user
                userDetailsService.deleteUser(username);
            }

            configurer
                .withUser(username)
                .password(passwordEncoder.encode(password))
                .roles(roles);
        });
    }
}
