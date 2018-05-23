package uk.gov.hmcts.reform.feature;

import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.Charsets;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.logging.appinsights.SyntheticHeaders;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@TestPropertySource("classpath:application.properties")//Requires for executing tests through IntelliJ IDE
public abstract class BaseTest {

    @Value("${test-url}")
    protected String testUrl;

    protected static final String SYNTHETIC_SOURCE_HEADER_VALUE = "Feature Toggle Smoke Test";

    protected static final String FF4J_STORE_FEATURES_URL = "api/ff4j/store/features/";

    protected String loadJson(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }

    protected RequestSpecification requestSpecification() {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.testUrl)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(SyntheticHeaders.SYNTHETIC_TEST_SOURCE, SYNTHETIC_SOURCE_HEADER_VALUE);
    }

    protected void createFeatureToggle(String featureUuid, String createRequestBody) {
        requestSpecification()
            .log().uri()
            .and()
            .body(createRequestBody.replace("{uid}", featureUuid))
            .when()
            .put(FF4J_STORE_FEATURES_URL + featureUuid)
            .then()
            .statusCode(201);
    }

    protected void deleteAllFeatures() {
        requestSpecification()
            .delete("/api/ff4j/store/clear");
    }
}
