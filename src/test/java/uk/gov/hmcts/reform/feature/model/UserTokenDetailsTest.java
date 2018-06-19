package uk.gov.hmcts.reform.feature.model;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTokenDetailsTest {

    @Test
    public void should_create_model_with_appropriate_list_of_grant_authorities() {
        // having
        List<String> idamRoles = ImmutableList.of("beta", "test", "user");

        // when
        UserTokenDetails details = new UserTokenDetails("ID", idamRoles);

        // then
        assertThat(details.getRoles()).hasOnlyElementsOfType(GrantedAuthority.class);
        assertThat(details.getRoles()).extracting("authority").hasSameElementsAs(idamRoles);
    }
}
