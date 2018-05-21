package uk.gov.hmcts.reform.feature.config;

import org.assertj.core.api.Assertions;
import org.ff4j.core.Feature;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc;
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
public class Ff4jFeatureStoreTest {

    private static final FeatureStoreSpringJdbc featureStore = new FeatureStoreSpringJdbc();

    @Autowired
    private DataSource dataSource;

    @Before
    public void initStore() {
        featureStore.setDataSource(dataSource);
        featureStore.importFeaturesFromXmlFile("ff4j.xml");
    }

    @Test
    public void should_contain_all_features_in_db_when_xml_is_imported_into_feature_store() {
        Feature awesomeFeature = featureStore.read("AwesomeFeature");

        assertThat(awesomeFeature.isEnable()).isTrue();

        assertThat(awesomeFeature.getDescription()).isEqualTo("awesome feature");

        assertThat(featureStore.read("cmc-shutter-page").isEnable()).isTrue();

        assertThat(featureStore.read("cmc-shutter-page").getDescription())
            .isEqualTo("feature to shutter front end");
    }

    @Test
    public void should_throw_feature_not_found_exception_when_feature_does_not_exists() {
        Assertions.assertThatThrownBy(() -> featureStore.read("doesnotexist"))
            .isInstanceOf(FeatureNotFoundException.class);
    }
}
