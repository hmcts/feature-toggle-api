package uk.gov.hmcts.reform.feature;

import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.Charsets;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.logging.appinsights.SyntheticHeaders;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@TestPropertySource("classpath:application.properties")//Requires for executing tests through IntelliJ IDE
public abstract class BaseTest {

    @Value("${test-url}")
    protected String testUrl;

    @Value("${test-admin-user}")
    protected String testAdminUser;

    @Value("${test-admin-password}")
    protected String testAdminPassword;

    @Value("${test-editor-user}")
    protected String testEditorUser;

    @Value("${test-editor-password}")
    protected String testEditorPassword;

    @Value("${test-read-user}")
    protected String testReadUser;

    @Value("${test-read-password}")
    protected String testReadPassword;

    protected static final String SYNTHETIC_SOURCE_HEADER_VALUE = "Feature Toggle Smoke Test";

    protected static final String FF4J_STORE_FEATURES_URL = "api/ff4j/store/features/";

    protected static final String FF4J_WEB_CONSOLE_URL = "ff4j-web-console/";

    protected String loadJson(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }

    @Before
    public void setUp() {
        RestAssured.authentication = RestAssured.basic(testAdminUser, testAdminPassword);
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
