package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.ff4j.audit.repository.EventRepository;
import org.ff4j.audit.repository.JdbcEventRepository;
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc;
import org.ff4j.springjdbc.store.PropertyStoreSpringJdbc;
import org.ff4j.web.FF4jDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.feature.FeatureProvider;

import java.util.Collections;
import javax.sql.DataSource;

import static org.ff4j.web.bean.WebConstants.SERVLETPARAM_FF4JPROVIDER;

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
    @ConditionalOnMissingBean
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

        // So far the implementation with SpringJDBC is not there, leverage on default JDBC
        EventRepository eventRepository = new JdbcEventRepository(dataSource);
        ff4j.setEventRepository(eventRepository);

        return ff4j;
    }

    @Bean
    public ServletRegistrationBean ff4jServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(
            new FF4jDispatcherServlet(),
            "/ff4j-console/*"
        );

        bean.setName("ff4j-console");
        bean.setLoadOnStartup(1);
        bean.setInitParameters(Collections.singletonMap(
            SERVLETPARAM_FF4JPROVIDER, FeatureProvider.class.getCanonicalName()
        ));

        return bean;
    }
}
