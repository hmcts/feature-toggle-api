package uk.gov.hmcts.reform.feature;

import io.restassured.path.json.JsonPath;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteFeatureToggleTest extends BaseTest {

    @Test
    public void should_successfully_delete_feature_toggle_from_feature_store() throws IOException {
        String featureUuid = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"));

        int statusCode = requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid)
            .getStatusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void should_return_response_containing_404_error_when_feature_does_not_exists_in_feature_store() {
        JsonPath jsonPath = requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + "doesnotexist").jsonPath();

        assertThat(jsonPath.getInt("status")).isEqualTo(404);
        assertThat(jsonPath.getString("error")).isEqualTo("Not Found");
        assertThat(jsonPath.getString("exception")).contains("FeatureNotFoundException");
    }
}
