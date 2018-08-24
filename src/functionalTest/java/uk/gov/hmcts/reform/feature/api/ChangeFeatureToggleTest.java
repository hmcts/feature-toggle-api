package uk.gov.hmcts.reform.feature.api;

import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;

import java.io.IOException;
import java.util.UUID;

public class ChangeFeatureToggleTest extends BaseTest {

    @Test
    public void should_successfully_enable_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"));

        //Enable feature toggle
        requestSpecification()
            .log().uri()
            .and()
            .when()
            .post(FF4J_STORE_FEATURES_URL + featureUuid + "/enable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }

    @Test
    public void should_successfully_disable_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-enabled.json"));

        //Disable feature toggle
        requestSpecification()
            .log().uri()
            .and()
            .when()
            .post(FF4J_STORE_FEATURES_URL + featureUuid + "/disable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }
}
