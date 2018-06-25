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

public class LoginTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_allow_user_to_login_and_redirected_to_home_page() {
        RequestSpecification specification = requestSpecification();

        String location = specification
            .contentType(ContentType.URLENC)
            .formParam("username", testEditorUser)
            .formParam("password", testEditorPassword)
            .post("/login")
            .then()
            .statusCode(FOUND.value())
            .extract()
            .header(LOCATION);

        assertThat(location).contains("/?login");
    }
}
