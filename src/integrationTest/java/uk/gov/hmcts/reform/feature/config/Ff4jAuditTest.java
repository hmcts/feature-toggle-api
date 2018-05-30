package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.ff4j.audit.EventQueryDefinition;
import org.ff4j.audit.EventSeries;
import org.ff4j.utils.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ff4j.audit.EventConstants.ACTION_CREATE;
import static org.ff4j.audit.EventConstants.ACTION_DELETE;
import static org.ff4j.audit.EventConstants.ACTION_TOGGLE_OFF;
import static org.ff4j.audit.EventConstants.ACTION_TOGGLE_ON;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ff4jAuditTest {

    @Autowired
    private FF4j ff4j;

    private static final UserDetails ANONYMOUS_USER = User
        .withUsername("anonymous")
        .password("")
        .roles("ANONYMOUS")
        .build();

    private static final EventQueryDefinition EVENT_QUERY_DEFINITION =
        new EventQueryDefinition(0, TimeUtils.getTomorrowMidnightTime());

    @Before
    public void setUp() {
        // make sure event repository is clean
        ff4j.getEventRepository().purgeAuditTrail(EVENT_QUERY_DEFINITION);
        // make sure audit is enabled
        assertThat(ff4j.isEnableAudit()).isTrue();

        // set up anonymous user for auditing check
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
            "anonymous-key",
            ANONYMOUS_USER,
            ANONYMOUS_USER.getAuthorities()
        ));
    }

    @Test
    public void should_audit_every_ff4j_action() {
        String name = "Some test feature";
        String description = "Some test description";

        // given
        ff4j.createFeature(name, true, description);
        ff4j.disable(name);
        ff4j.enable(name);
        ff4j.delete(name);

        // when
        EventSeries events = ff4j.getEventRepository().getAuditTrail(EVENT_QUERY_DEFINITION);

        // then
        assertThat(events).extracting("action").containsExactlyInAnyOrder(
            ACTION_CREATE, ACTION_DELETE, ACTION_TOGGLE_OFF, ACTION_TOGGLE_ON
        );
        assertThat(events).extracting("user").containsExactlyElementsOf(
            Collections.nCopies(4, ANONYMOUS_USER.getUsername())
        );
    }
}
