package uk.gov.hmcts.reform.feature;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import java.io.IOException;
import java.util.UUID;

public class CreateFeatureToggleTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_successfully_create_feature_toggle_in_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = "smoke-test-" + UUID.randomUUID();

        String createRequestBody = loadJson("feature-toggle-enabled.json");

        requestSpecification()
            .log().uri()
            .and()
            .body(createRequestBody.replace("{uid}", featureUuid))
            .when()
            .put(API_FF4J_STORE_FEATURES + featureUuid)
            .then()
            .statusCode(201);

        //Delete the created feature
        requestSpecification()
            .delete(API_FF4J_STORE_FEATURES + featureUuid);
    }
}
