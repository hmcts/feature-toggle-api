package uk.gov.hmcts.reform.feature;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        "users.readers[0].username=admintest@hmcts.net",
        "users.readers[0].password=admin123",
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

    @Test
    public void should_not_allow_user_to_access_webconsole_when_admin_user_is_changed_to_read_user() throws Exception {
        mockMvc
            .perform(post("/login")
                .param("username", "admintest@hmcts.net")
                .param("password", "admin123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(status().is(HttpStatus.valueOf(302).value()))
            .andExpect(redirectedUrl("/?login"));
    }

    @Test
    public void should_allow_new_user_to_access_webconsole_when_configured_as_admin() throws Exception {
        mockMvc
            .perform(post("/login")
                .param("username", "admintest1@hmcts.net")
                .param("password", "admin456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
            .andExpect(status().is(HttpStatus.valueOf(302).value()))
            .andExpect(redirectedUrl(FF4J_WEB_CONSOLE_URL));
    }

    @Test
    public void should_allow_new_user_to_create_feature_toggle_when_configured_as_editor() throws Exception {
        final String auth = "Basic " + Base64Utils.encodeToString("editortest1@hmcts.net:editor456".getBytes());
        String featureUid = UUID.randomUUID().toString();

        JSONObject createFeatureJson = new JSONObject();
        createFeatureJson.put("description", "Feature toggle for test");
        createFeatureJson.put("enable", "true");
        createFeatureJson.put("uid", featureUid);

        mockMvc
            .perform(
                put(FF4J_STORE_FEATURES_URL + featureUid)
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .content(createFeatureJson.toString())
            ).andExpect(status().isCreated());
    }
}
