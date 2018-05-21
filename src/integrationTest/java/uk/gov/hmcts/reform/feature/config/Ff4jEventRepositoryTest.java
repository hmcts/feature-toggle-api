package uk.gov.hmcts.reform.feature.config;

import org.ff4j.audit.Event;
import org.ff4j.audit.repository.EventRepository;
import org.ff4j.audit.repository.JdbcEventRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ff4jEventRepositoryTest {

    private EventRepository eventRepository;

    private static final LocalDateTime localDateTime = LocalDateTime.of(2018, 5, 21, 18, 41, 16);

    @Autowired
    private DataSource dataSource;

    @Test
    public void should_return_event_with_all_details_when_event_exists_in_event_store() {
        EventRepository eventRepository = new JdbcEventRepository(dataSource);
        eventRepository.saveEvent(createEvent());

        Event cmcFeatureEnableEvent = eventRepository.getEventByUUID("cmc-shutter-page", Timestamp.valueOf(localDateTime).getTime());
        assertThat(cmcFeatureEnableEvent.getName()).isEqualTo("Feature Enable event");
        assertThat(cmcFeatureEnableEvent.getAction()).isEqualTo("enableFeature");
        assertThat(cmcFeatureEnableEvent.getHostName()).isEqualTo("localhost");
        assertThat(cmcFeatureEnableEvent.getUser()).isEqualTo("cmc-admin");
        assertThat(cmcFeatureEnableEvent.getType()).isEqualTo("test");
        assertThat(cmcFeatureEnableEvent.getSource()).isEqualTo("test");
    }

    @Test
    public void should_return_null_when_event_does_not_exists() {
        EventRepository eventRepository = new JdbcEventRepository(dataSource);

        Event cmcFeatureEnableEvent = eventRepository.getEventByUUID("doesnotexist", Timestamp.valueOf(localDateTime).getTime());
        assertThat(cmcFeatureEnableEvent).isNull();
    }

    private Event createEvent() {
        Event event = new Event();
        event.setName("Feature Enable event");
        event.setAction("enableFeature");
        event.setHostName("localhost");
        event.setUuid("cmc-shutter-page");
        event.setUser("cmc-admin");
        event.setType("test");
        event.setSource("test");
        event.setTimestamp(Timestamp.valueOf(localDateTime).getTime());
        return event;
    }
}
