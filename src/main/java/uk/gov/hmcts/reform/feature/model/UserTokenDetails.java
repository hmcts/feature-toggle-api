package uk.gov.hmcts.reform.feature.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserTokenDetails {

    public final String id;

    public final List<? extends GrantedAuthority> roles;

    public UserTokenDetails(String id, List<String> roles) {
        this.id = id;
        this.roles = roles
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
