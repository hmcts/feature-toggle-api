package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.ff4j.audit.EventQueryDefinition;
import org.ff4j.audit.EventSeries;
import org.ff4j.core.Feature;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.utils.TimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ff4j.audit.EventConstants.ACTION_CREATE;
import static org.ff4j.audit.EventConstants.ACTION_DELETE;
import static org.ff4j.audit.EventConstants.ACTION_TOGGLE_OFF;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ff4jAuditTest {

    @Autowired
    private FF4j ff4j;

    private static final EventQueryDefinition EVENT_QUERY_DEFINITION =
        new EventQueryDefinition(0, TimeUtils.getTomorrowMidnightTime());

    @Before
    public void setUp() {
        // make sure event repository is clean
        ff4j.getEventRepository().purgeAuditTrail(EVENT_QUERY_DEFINITION);
        // make sure audit is enabled
        assertThat(ff4j.isEnableAudit()).isTrue();
    }

    @Test
    public void should_audit_every_ff4j_action() {
        String name = "Some test feature";
        String description = "Some test description";

        // given
        Feature newFeature = ff4j.createFeature(name, true, description).getFeature(name);
        Feature disabledNewFeature = ff4j.disable(name).getFeature(name);
        Feature deletedNewFeature = null;
        try {
            deletedNewFeature = ff4j.delete(name).getFeature(name);
        } catch (FeatureNotFoundException exception) {
            // nothing
        }

        // when
        assertThat(newFeature.isEnable()).isTrue();
        assertThat(disabledNewFeature.isEnable()).isFalse();
        assertThat(deletedNewFeature).isNull();

        // then
        EventSeries events = ff4j.getEventRepository().getAuditTrail(EVENT_QUERY_DEFINITION);
        assertThat(events.size()).isEqualTo(3); // only amendments count

        // and
        assertThat(events).extracting("action").containsExactlyInAnyOrder(
            ACTION_CREATE, ACTION_DELETE, ACTION_TOGGLE_OFF
        );
        assertThat(events).extracting("user").containsExactlyElementsOf(
            Collections.nCopies(3, null) // todo
        );
    }
}
