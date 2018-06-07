package uk.gov.hmcts.reform.feature;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
    properties = {
        "flyway.enabled=false"
    }
)
public class ApplicationTests {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void should_load_in_memory_user_details_manager_when_flyway_is_disabled() {
        assertThat(userDetailsService).isInstanceOf(InMemoryUserDetailsManager.class);
    }

}
