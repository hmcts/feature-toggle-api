package uk.gov.hmcts.reform.feature.model;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRolesTest {

    @Test
    public void should_create_model_with_appropriate_list_of_grant_authorities() {
        // having
        String[] roles = new String[] { "beta", "test", "user" };

        // when
        UserRoles userRoles = new UserRoles("ID", roles);

        // then
        assertThat(userRoles.getRoles()).hasOnlyElementsOfType(GrantedAuthority.class);
        assertThat(userRoles.getRoles()).extracting("authority").hasSameElementsAs(Arrays.asList(roles));
    }
}
