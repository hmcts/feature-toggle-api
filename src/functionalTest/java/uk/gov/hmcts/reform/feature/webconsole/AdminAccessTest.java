package uk.gov.hmcts.reform.feature.webconsole;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;

public class AdminAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_allow_admin_to_login_to_ff4j_web_console() {
        RequestSpecification specification = requestSpecification();

        String location = specification
            .contentType(ContentType.URLENC)
            .formParam("username", "admin")
            .formParam("password", "admin")
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .header(LOCATION);

        assertThat(location).contains(FF4J_WEB_CONSOLE_URL);
    }
}
