package uk.gov.hmcts.reform.feature.config;

import org.assertj.core.api.Assertions;
import org.ff4j.exception.PropertyNotFoundException;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyString;
import org.ff4j.springjdbc.store.PropertyStoreSpringJdbc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ff4jPropertyStoreTest {
    private static final PropertyStoreSpringJdbc propertyStore = new PropertyStoreSpringJdbc();

    @Autowired
    private DataSource dataSource;

    @Before
    public void initStore() {
        propertyStore.setDataSource(dataSource);
        propertyStore.createProperty(new PropertyString("testA", "testValueA"));
    }

    @Test
    public void should_return_property_with_values_when_property_exists_in_property_store() {
        Property<?> testProperty = propertyStore.readProperty("testA");

        assertThat(propertyStore.existProperty("testA")).isTrue();
        
        assertThat(testProperty.getName()).isEqualTo("testA");
        assertThat(testProperty.getType()).isEqualTo("org.ff4j.property.PropertyString");
        assertThat(testProperty.getValue()).isEqualTo("testValueA");
    }

    @Test
    public void should_throw_property_not_found_exception_when_feature_does_not_exists() {
        Assertions.assertThatThrownBy(() -> propertyStore.readProperty("doesnotexist"))
            .isInstanceOf(PropertyNotFoundException.class);
    }
}
