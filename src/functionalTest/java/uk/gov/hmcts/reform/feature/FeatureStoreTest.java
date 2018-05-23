package uk.gov.hmcts.reform.feature;

import io.restassured.path.json.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureStoreTest extends BaseTest {

    @Test
    public void should_return_feature_store_details() throws IOException {
        //Delete all features in the feature store else the last one is returned by default
        //Functional tests are not executed on prod slot so should be safe to do this.
        deleteAllFeatures();

        String featureUuid1 = UUID.randomUUID().toString();
        String featureUuid2 = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid1, loadJson("feature-toggle-disabled.json"));
        createFeatureToggle(featureUuid2, loadJson("feature-toggle-disabled.json"));

        JsonPath jsonPath = requestSpecification()
            .get("/api/ff4j/store").jsonPath();

        assertThat(jsonPath.getString("type")).isEqualTo("org.ff4j.springjdbc.store.FeatureStoreSpringJdbc");
        assertThat(jsonPath.getInt("numberOfFeatures")).isEqualTo(2);
        assertThat(jsonPath.getString("cache")).isNull();
        assertThat(jsonPath.getList("features")).containsExactly(featureUuid1, featureUuid2);

        requestSpecification()
            .delete(API_FF4J_STORE_FEATURES + featureUuid1);
        requestSpecification()
            .delete(API_FF4J_STORE_FEATURES + featureUuid2);
    }
}
