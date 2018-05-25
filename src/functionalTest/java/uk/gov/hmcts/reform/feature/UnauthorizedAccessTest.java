package uk.gov.hmcts.reform.feature;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnauthorizedAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_anonymous_access_to_api_ff4j() {
        requestSpecification()
            .auth().none()
            .get(FF4J_STORE_FEATURES_URL + "doesnotexist")
            .then()
            .statusCode(UNAUTHORIZED.value());
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_anonymous_access_to_ff4j_web_console() {
        requestSpecification()
            .auth().none()
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(UNAUTHORIZED.value());
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_access_for_read_only_user_to_access_write_api_endpoints() {
        requestSpecification()
            .auth().basic("user", "password")
            .delete(FF4J_STORE_FEATURES_URL + "doesnotexist")
            .then()
            .statusCode(FORBIDDEN.value());
    }

    @Category(SmokeTestCategory.class)
    @Test
    public void should_restrict_access_for_user_to_access_ff4j_web_console() {
        requestSpecification()
            .auth().basic("user", "password")
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(FORBIDDEN.value());
    }
}
