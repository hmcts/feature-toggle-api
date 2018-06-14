package uk.gov.hmcts.reform.feature.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenDetails {

    public final String id;

    public final String email;

    public final Set<String> roles;

    public UserTokenDetails(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("roles") Set<String> roles
    ) {
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}
