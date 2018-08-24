package uk.gov.hmcts.reform.feature.webconsole;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AdminAccessTest extends BaseTest {

    @Test
    public void should_not_allow_access_for_non_admin_user() {
        RequestSpecification specification = requestSpecification();
        // Explicitly state that you don't want to use any authentication in this request.
        specification.auth().none();

        Cookies cookies = specification
            .contentType(ContentType.URLENC)
            .formParam("username", testEditorUser)
            .formParam("password", testEditorPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .response()
            .getDetailedCookies();

        specification
            .cookies(cookies)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("Error"));
    }

    @Test
    public void should_verify_login_logout_journey() {
        RequestSpecification specification = requestSpecification();
        // Explicitly state that you don't want to use any authentication in this request.
        specification.auth().none();

        Cookies cookies = specification
            .contentType(ContentType.URLENC)
            .formParam("username", testAdminUser)
            .formParam("password", testAdminPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .response()
            .getDetailedCookies();

        specification
            .cookies(cookies)
            .get(FF4J_WEB_CONSOLE_URL)
            .then()
            .statusCode(OK.value())
            .body("html.head.title", equalTo("FF4J - Home"));

        specification
            .cookies(cookies)
            .get("/logout")
            .then()
            .statusCode(OK.value());

        specification
            .cookies(cookies)
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
