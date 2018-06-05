package uk.gov.hmcts.reform.feature.webconsole;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AdminAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_verify_login_logour_journey() {
        RequestSpecification specification = requestSpecification();
        String sessionCookieName = "JSESSIONID";

        String sessionCookieValue = specification
            .contentType(ContentType.URLENC)
            .formParam("username", testAdminUser)
            .formParam("password", testAdminPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .cookie(sessionCookieName);

        specification
            .cookie(sessionCookieName, sessionCookieValue)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("FF4J - Home"));

        specification
            .cookie(sessionCookieName, sessionCookieValue)
            .get("/logout")
            .then()
            .statusCode(OK.value());

        specification
            .cookie(sessionCookieName, sessionCookieValue)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("Login Page"));
    }

    @Test
    public void should_allow_admin_to_login_to_ff4j_web_console() {
        RequestSpecification specification = requestSpecification();

        String location = specification
            .contentType(ContentType.URLENC)
            .formParam("username", testAdminUser)
            .formParam("password", testAdminPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .header(LOCATION);

        assertThat(location).contains(FF4J_WEB_CONSOLE_URL);
    }
}
