package uk.gov.hmcts.reform.feature.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserRoles {

    private final String id;

    private final List<GrantedAuthority> authorities;

    public UserRoles(String id, String[] roles) {
        this.id = id;
        this.authorities = Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
