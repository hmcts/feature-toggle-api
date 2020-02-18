package uk.gov.hmcts.reform.feature;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * This class tests dynamic configuration of users and roles.
 * For integration tests initial data is loaded via initial-user-roles-load.sql
 * before spring security configures users in database.
 * Values are loaded in this order Initial data load from sql -> application yaml config -> Override using test property
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
        "users.admins[0].username=admintest1@hmcts.net",
        "users.admins[0].password=admin456",
        "users.editors[0].username=editortest1@hmcts.net",
        "users.editors[0].password=editor456"
    })
public class DynamicUserAndRolesIntegrationTest {
    private static final String FF4J_WEB_CONSOLE_URL = "/ff4j-web-console/home";
    private static final String FF4J_STORE_FEATURES_URL = "/api/ff4j/store/features/";

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webAppContext;

    @Before
    public void setup() {
        WebRequestTrackingFilter filter = new WebRequestTrackingFilter();
        filter.init(new MockFilterConfig()); // using a mock that you construct with init params and all
        this.mockMvc = webAppContextSetup(this.webAppContext)
            .apply(springSecurity())
            .addFilters(filter).build();
    }

    @Test
    public void should_allow_new_user_to_access_webconsole_when_configured_as_admin() throws Exception {
        mockMvc
            .perform(formLogin().user("admintest1@hmcts.net").password("admin456"))
            .andExpect(status().is(HttpStatus.FOUND.value()))
            .andExpect(redirectedUrl(FF4J_WEB_CONSOLE_URL));
    }

    @Test
    public void should_allow_new_user_to_create_feature_toggle_when_configured_as_editor() throws Exception {
        String featureUid = UUID.randomUUID().toString();

        JSONObject createFeatureJson = new JSONObject();
        createFeatureJson.put("description", "Feature toggle for test");
        createFeatureJson.put("enable", "true");
        createFeatureJson.put("uid", featureUid);

        mockMvc
            .perform(
                put(FF4J_STORE_FEATURES_URL + featureUid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(createFeatureJson.toString())
                    .with(httpBasic("editortest1@hmcts.net", "editor456"))
            ).andExpect(status().isCreated());
    }
}
