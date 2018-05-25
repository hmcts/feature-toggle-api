package uk.gov.hmcts.reform.feature.webconsole;

import io.restassured.response.Response;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminAccessTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_allow_admin_to_access_ff4j_web_console() {
        Response response = requestSpecification()
            .get(FF4J_WEB_CONSOLE_URL)
            .andReturn();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().htmlPath().getString("html.head.title")).isEqualTo("FF4J - Home");
    }
}
