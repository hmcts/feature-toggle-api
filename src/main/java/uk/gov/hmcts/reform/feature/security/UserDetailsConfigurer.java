package uk.gov.hmcts.reform.feature.security;

import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import rx.functions.Action3;
import uk.gov.hmcts.reform.feature.webconsole.Ff4jUsersConfig;
import uk.gov.hmcts.reform.feature.webconsole.Ff4jUsersConfig.UserDetails;

import java.util.List;

import static uk.gov.hmcts.reform.feature.security.Roles.ADMIN;
import static uk.gov.hmcts.reform.feature.security.Roles.EDITOR;
import static uk.gov.hmcts.reform.feature.security.Roles.USER;

public class UserDetailsConfigurer {

    private final UserDetailsManager manager;

    private final Action3<String, String, String[]> createUser;

    public UserDetailsConfigurer(UserDetailsManagerConfigurer<?, ?> configurer, PasswordEncoder passwordEncoder) {
        this.manager = configurer.getUserDetailsService();
        this.createUser = (String username, String password, String... roles) -> configurer
            .withUser(username)
            .password(passwordEncoder.encode(password))
            .roles(roles);
    }

    public void configure(Ff4jUsersConfig userConfig) {
        configureUsers(userConfig.getAdmins(), ADMIN, EDITOR);
        configureUsers(userConfig.getEditors(), EDITOR);
        configureUsers(userConfig.getReaders(), USER);
    }

    private void configureUsers(List<UserDetails> userDetails, String... roles) {
        userDetails.forEach(user -> {
            final String username = user.getUsername();
            final String password = user.getPassword();

            if (manager.userExists(username)) {
                //This will delete authorities and then user
                manager.deleteUser(username);
            }

            createUser.call(username, password, roles);
        });
    }
}
