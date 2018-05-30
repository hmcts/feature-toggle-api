package uk.gov.hmcts.reform.feature;

import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetFeatureToggleTest extends BaseTest {

    @Test
    public void should_successfully_retrieve_feature_toggle_from_feature_store() throws IOException {
        //Feature name should be unique in the feature store
        String featureUuid = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"));

        JsonPath jsonPath = requestSpecification()
            .auth().none()
            .get(FF4J_STORE_FEATURES_URL + featureUuid).jsonPath();

        assertThat(jsonPath.getString("uid")).isEqualTo(featureUuid);
        assertThat(jsonPath.getBoolean("enable")).isFalse();
        assertThat(jsonPath.getString("description")).isEqualTo("Feature toggle for test");

        //Delete the created feature
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }

    @Test
    public void should_return_response_body_containing_404_exception_when_feature_does_not_exists() {
        JsonPath jsonPath = requestSpecification()
            .auth().none()
            .get(FF4J_STORE_FEATURES_URL + "doesnotexist").jsonPath();

        assertThat(jsonPath.getInt("status")).isEqualTo(404);
        assertThat(jsonPath.getString("error")).isEqualTo("Not Found");
        assertThat(jsonPath.getString("exception")).contains("FeatureNotFoundException");
    }

    @Test
    public void should_throw_feature_id_blank_exception_when_feature_id_is_blank() {

        //Delete all features in the feature store else the last one is returned by default
        //Functional tests are not executed on prod slot so should be safe to do this.
        deleteAllFeatures();

        requestSpecification()
            .get(FF4J_STORE_FEATURES_URL)
            .then()
            .body("$", Matchers.empty());
    }
}
