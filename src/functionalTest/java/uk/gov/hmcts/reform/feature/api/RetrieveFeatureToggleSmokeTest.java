package uk.gov.hmcts.reform.feature.api;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.categories.SmokeTestCategory;

import java.io.IOException;

public class RetrieveFeatureToggleSmokeTest extends BaseTest {

    @Category(SmokeTestCategory.class)
    @Test
    public void should_return_all_feature_toggles_from_feature_store() throws IOException {
        Response response = requestSpecification()
            .get("/api/ff4j/store/features");

        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
