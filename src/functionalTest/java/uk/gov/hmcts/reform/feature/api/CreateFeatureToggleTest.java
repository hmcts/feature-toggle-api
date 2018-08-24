package uk.gov.hmcts.reform.feature.api;

import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;

import java.io.IOException;
import java.util.UUID;

public class CreateFeatureToggleTest extends BaseTest {

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
            .put(FF4J_STORE_FEATURES_URL + featureUuid)
            .then()
            .statusCode(201);

        //Delete the created feature
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }
}
