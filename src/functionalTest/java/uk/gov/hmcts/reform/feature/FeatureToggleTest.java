package uk.gov.hmcts.reform.feature;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import java.io.IOException;
import java.util.UUID;

public class FeatureToggleTest extends BaseTest {


    @Category(SmokeTestCategory.class)
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
            .post(API_FF4J_STORE_FEATURES + featureUuid + "/enable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete(API_FF4J_STORE_FEATURES + featureUuid);
    }

    @Category(SmokeTestCategory.class)
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
            .post(API_FF4J_STORE_FEATURES + featureUuid + "/disable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete(API_FF4J_STORE_FEATURES + featureUuid);
    }
}
