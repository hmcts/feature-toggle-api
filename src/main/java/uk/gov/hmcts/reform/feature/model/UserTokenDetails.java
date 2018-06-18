package uk.gov.hmcts.reform.feature.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserTokenDetails {

    private final String id;

    private final List<? extends GrantedAuthority> roles;

    public UserTokenDetails(String id, List<String> roles) {
        this.id = id;
        this.roles = roles
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public List<? extends GrantedAuthority> getRoles() {
        return roles;
    }
}
