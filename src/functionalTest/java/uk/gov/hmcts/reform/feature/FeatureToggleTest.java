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
            .post("api/ff4j/store/features/" + featureUuid + "/enable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete("api/ff4j/store/features/" + featureUuid);
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
            .post("api/ff4j/store/features/" + featureUuid + "/disable")
            .then()
            .statusCode(202);

        //Delete the created feature
        requestSpecification()
            .delete("api/ff4j/store/features/" + featureUuid);

    }

    private void createFeatureToggle(String featureUuid, String createRequestBody) {
        requestSpecification()
            .log().uri()
            .and()
            .body(createRequestBody.replace("{uid}", featureUuid))
            .when()
            .put("api/ff4j/store/features/" + featureUuid)
            .then()
            .statusCode(201);
    }
}
