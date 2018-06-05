package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.ff4j.audit.repository.EventRepository;
import org.ff4j.security.SpringSecurityAuthorisationManager;
import org.ff4j.springjdbc.store.EventRepositorySpringJdbc;
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc;
import org.ff4j.springjdbc.store.PropertyStoreSpringJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@ComponentScan(value = {
    "org.ff4j.spring.boot.web.api",
    "org.ff4j.services",
    "org.ff4j.aop",
    "org.ff4j.spring"
})
@ConditionalOnClass(FF4j.class)
@Configuration
public class Ff4jConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public FF4j getFF4j() {
        FF4j ff4j = new FF4j();

        // Feature States in a RDBMS
        FeatureStoreSpringJdbc featureStore = new FeatureStoreSpringJdbc();
        featureStore.setDataSource(dataSource);
        ff4j.setFeatureStore(featureStore);

        // Properties in RDBMS
        PropertyStoreSpringJdbc propertyStore = new PropertyStoreSpringJdbc();
        propertyStore.setDataSource(dataSource);
        ff4j.setPropertiesStore(propertyStore);

        // Events in RDBMS
        EventRepository eventRepository = new EventRepositorySpringJdbc(dataSource);
        ff4j.setEventRepository(eventRepository);
        ff4j.setEnableAudit(true);

        // Set authorisation manager
        ff4j.setAuthorizationsManager(new SpringSecurityAuthorisationManager());

        return ff4j;
    }
}
