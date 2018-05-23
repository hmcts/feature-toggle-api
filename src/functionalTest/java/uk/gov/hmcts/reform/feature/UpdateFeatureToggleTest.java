package uk.gov.hmcts.reform.feature;

import io.restassured.path.json.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateFeatureToggleTest extends BaseTest {

    // TODO:Add Group,Roles and Strategy test cases as part of Security Story

    @Test
    public void should_successfully_update_feature_toggle_description_in_feature_store() throws IOException {
        String featureUuid = UUID.randomUUID().toString();

        createFeatureToggle(featureUuid, loadJson("feature-toggle-disabled.json"));

        //Update feature toggle description
        requestSpecification()
            .log().uri()
            .and()
            .body(loadJson("feature-toggle-update.json").replace("{uid}", featureUuid))
            .when()
            .put(FF4J_STORE_FEATURES_URL + featureUuid);

        //Retrieve updated feature toggle
        JsonPath jsonPath = requestSpecification()
            .get(FF4J_STORE_FEATURES_URL + featureUuid).jsonPath();

        assertThat(jsonPath.getString("uid")).isEqualTo(featureUuid);
        assertThat(jsonPath.getBoolean("enable")).isFalse();
        assertThat(jsonPath.getString("description")).isEqualTo("Updated feature toggle description for test");

        //Delete the created feature
        requestSpecification()
            .delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }
}
