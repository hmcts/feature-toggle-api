package uk.gov.hmcts.reform.feature.api;

import io.restassured.path.json.JsonPath;
import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureStoreTest extends BaseTest {

    @Test
    public void should_return_feature_store_details() throws IOException {
        String featureUuid1 = UUID.randomUUID().toString();
        String featureUuid2 = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid1, loadJson("feature-toggle-disabled.json"));
        createFeatureToggle(featureUuid2, loadJson("feature-toggle-disabled.json"));

        JsonPath jsonPath = requestSpecification()
            .get("/api/ff4j/store").jsonPath();

        assertThat(jsonPath.getString("type")).isEqualTo("org.ff4j.audit.proxy.FeatureStoreAuditProxy");
        //There might be other features in feature store but there should be at least 2 as we are creating it
        assertThat(jsonPath.getInt("numberOfFeatures")).isGreaterThanOrEqualTo(2);
        assertThat(jsonPath.getString("cache")).isNull();
        assertThat(jsonPath.getList("features")).contains(featureUuid1, featureUuid2);

        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid1);
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid2);
    }
}
