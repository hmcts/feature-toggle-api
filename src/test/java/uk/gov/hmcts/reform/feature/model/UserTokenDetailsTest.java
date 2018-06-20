package uk.gov.hmcts.reform.feature.model;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTokenDetailsTest {

    @Test
    public void should_create_model_with_appropriate_list_of_grant_authorities() {
        // having
        String[] userRoles = new String[] { "beta", "test", "user" };

        // when
        UserTokenDetails details = new UserTokenDetails("ID", userRoles);

        // then
        assertThat(details.getRoles()).hasOnlyElementsOfType(GrantedAuthority.class);
        assertThat(details.getRoles()).extracting("authority").hasSameElementsAs(Arrays.asList(userRoles));
    }
}
