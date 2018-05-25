package uk.gov.hmcts.reform.feature;

import io.restassured.RestAssured;
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

    @Test
    public void should_allow_to_create_update_and_delete_feature_with_editor_access_levels() throws IOException {
        RestAssured.authentication = RestAssured.basic("master", "password");

        String featureUuid = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"));

        //Retrieve updated feature toggle
        JsonPath jsonPath1 = requestSpecification()
            .get(FF4J_STORE_FEATURES_URL + featureUuid).jsonPath();

        assertThat(jsonPath1.getString("uid")).isEqualTo(featureUuid);
        assertThat(jsonPath1.getBoolean("enable")).isFalse();
        assertThat(jsonPath1.getString("description")).isEqualTo("Feature toggle for test");

        requestSpecification()
            .log().uri()
            .and()
            .when()
            .post(FF4J_STORE_FEATURES_URL + featureUuid + "/enable")
            .then()
            .statusCode(202);

        //Retrieve updated feature toggle
        JsonPath jsonPath2 = requestSpecification()
            .get(FF4J_STORE_FEATURES_URL + featureUuid).jsonPath();

        assertThat(jsonPath2.getString("uid")).isEqualTo(featureUuid);
        assertThat(jsonPath2.getBoolean("enable")).isTrue();
        assertThat(jsonPath2.getString("description")).isEqualTo("Feature toggle for test");

        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
