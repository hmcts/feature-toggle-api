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

    private UserContainer[] users;

    private static class UserContainer {

        final String username;

        final String password;

        final boolean isUser;

        UserContainer() {
            username = null;
            password = null;
            isUser = false;
        }

        UserContainer(String username, String password) {
            this.username = username;
            this.password = password;
            isUser = true;
        }
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();

        users = new UserContainer[] {
            new UserContainer(),
            new UserContainer(super.testReadUser, super.testReadPassword),
            new UserContainer(super.testEditorUser, super.testEditorPassword),
            new UserContainer(super.testAdminUser, super.testAdminPassword)
        };

        featureUuid = UUID.randomUUID().toString();
    }

    @After
    public void tearDown() {
        requestSpecification().delete(FF4J_STORE_FEATURES_URL + featureUuid);
    }

    @Test
    public void should_return_feature_as_disabled_when_no_custom_headers_present() throws IOException {
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        for (UserContainer user : users) {
            RequestSpecification specification = prepareSpecification(user);
            checkToggle(specification, false);
        }
    }

    @Test
    public void should_return_feature_as_disabled_when_roles_do_not_match() throws IOException {
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        for (UserContainer user : users) {
            RequestSpecification specification = prepareSpecification(user)
                .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
                .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta_tester");
            checkToggle(specification, false);
        }
    }

    @Test
    public void should_return_feature_as_enabled_when_roles_match() throws IOException {
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-permissions.json"));

        for (UserContainer user : users) {
            RequestSpecification specification = prepareSpecification(user)
                .header(CustomUserPermissionsFilter.USER_ID_HEADER, SOME_USER)
                .header(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER, "beta");
            checkToggle(specification, true);
        }
    }

    @Test
    public void should_return_feature_as_enabled_when_builtin_role_match() throws IOException {
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-builtin-roles.json"));

        RequestSpecification specification = prepareSpecification(new UserContainer(testAdminUser, testAdminPassword));
        checkToggle(specification, true);
    }

    @Test
    public void should_return_feature_as_disabled_when_builtin_role_do_not_match() throws IOException {
        createFeatureToggle(featureUuid, loadJson("feature-toggle-with-builtin-roles.json"));

        RequestSpecification specification = prepareSpecification(
            new UserContainer(testEditorUser, testEditorPassword)
        );
        checkToggle(specification, false);
    }

    private RequestSpecification prepareSpecification(UserContainer user) {
        RequestSpecification specification = requestSpecification();

        if (user.isUser) {
            specification = specification.auth().preemptive().basic(user.username, user.password);
        } else {
            specification = specification.auth().none();
        }

        return specification;
    }

    private void checkToggle(RequestSpecification specification, boolean isOn) {
        String response = specification
            .get("api/ff4j/check/" + featureUuid)
            .then()
            .statusCode(OK.value())
            .extract()
            .body()
            .asString();

        assertThat(response).isEqualTo(String.valueOf(isOn));
    }
}
