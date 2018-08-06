package uk.gov.hmcts.reform.feature.api;

import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.feature.BaseTest;
import uk.gov.hmcts.reform.feature.security.CustomUserPermissionsFilter;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class RunAsUserTest extends BaseTest {

    private static final String SOME_USER = "functional-user@test";

    private String featureUuid;

    @Before
    public void setUp() {
        featureUuid = UUID.randomUUID().toString();
    }

    @After
    public void tearDown() {
        requestSpecification().delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }

    @Test
    public void should_return_feature_as_disabled_when_no_custom_headers_present() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification().auth().none());

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_disabled_when_no_custom_headers_and_editor_session_is_present()
        throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth()
            .preemptive()
            .basic(testEditorUser, testEditorPassword)
        );

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_disabled_when_no_custom_headers_and_admin_session_is_present()
        throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth()
            .preemptive()
            .basic(testAdminUser, testAdminPassword)
        );

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_disabled_when_roles_do_not_match() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().none()
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta_tester")
        );

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_disabled_when_roles_do_not_match_and_editor_session_is_present()
        throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().preemptive().basic(testEditorUser, testEditorPassword)
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta_tester")
        );

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_disabled_when_roles_do_not_match_and_admin_session_is_present()
        throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().preemptive().basic(testAdminUser, testAdminPassword)
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta_tester")
        );

        // then
        assertThat(response).isFalse();
    }

    @Test
    public void should_return_feature_as_enabled_when_roles_match() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().none()
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta")
        );

        // then
        assertThat(response).isTrue();
    }

    @Test
    public void should_return_feature_as_enabled_when_roles_match_and_editor_session_is_present() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().preemptive().basic(testEditorUser, testEditorPassword)
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta")
        );

        // then
        assertThat(response).isTrue();
    }

    @Test
    public void should_return_feature_as_enabled_when_roles_match_and_admin_session_is_present() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth().preemptive().basic(testAdminUser, testAdminPassword)
            .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
            .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta")
        );

        // then
        assertThat(response).isTrue();
    }

    @Test
    public void should_return_feature_as_enabled_when_builtin_role_match() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-builtin-roles.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth()
            .preemptive()
            .basic(testAdminUser, testAdminPassword)
        );

        // then
        assertThat(response).isTrue();
    }

    @Test
    public void should_return_feature_as_disabled_when_builtin_role_do_not_match() throws IOException {
        // given
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-builtin-roles.json"));

        // when
        boolean response = checkToggle(requestSpecification()
            .auth()
            .preemptive()
            .basic(testEditorUser, testEditorPassword)
        );

        // then
        assertThat(response).isFalse();
    }

    private boolean checkToggle(RequestSpecification specification) {
        return specification
            .get("api/ff4j/check/" + featureUuid)
            .then()
            .statusCode(OK.value())
            .extract()
            .body()
            .as(Boolean.class);
    }
}
