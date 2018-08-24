package uk.gov.hmcts.reform.feature.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;
import uk.gov.hmcts.reform.logging.appinsights.SyntheticHeaders;

public class RetrieveFeatureToggleSmokeTest extends BaseTest {

    private static final String SYNTHETIC_SOURCE_HEADER_VALUE = "Feature Toggle Smoke Test";

    @Category(SmokeTestCategory.class)
    @Test
    public void should_return_200_status_code_when_all_feature_toggles_are_retrieved_from_feature_store() {
        Response response = RestAssured
            .given()
            .baseUri(this.testUrl)
            .relaxedHTTPSValidation()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(SyntheticHeaders.SYNTHETIC_TEST_SOURCE, SYNTHETIC_SOURCE_HEADER_VALUE)
            .get("/api/ff4j/store/features");

        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
