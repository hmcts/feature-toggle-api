package uk.gov.hmcts.reform.feature;

import org.junit.Test;

import static org.springframework.http.HttpStatus.OK;

public class TmpHealthCheckSmokeTest extends SmokeTestSuite {

    @Test
    public void should_get_success_from_health_endpoint() {
        getCommonRequestSpec()
            .get("/health")
            .then()
            .statusCode(OK.value());
    }
}
