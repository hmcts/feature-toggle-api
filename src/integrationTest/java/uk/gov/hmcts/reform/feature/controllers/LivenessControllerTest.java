package uk.gov.hmcts.reform.feature.controllers;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LivenessControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webAppContext;

    @Before
    public void setup() {
        WebRequestTrackingFilter filter = new WebRequestTrackingFilter();
        filter.init(new MockFilterConfig()); // using a mock that you construct with init params and all
        this.mvc = webAppContextSetup(this.webAppContext)
            .addFilters(filter).build();
    }

    @Test
    public void givenAppIsRunning_whenGetLiveness_thenReturnsUp()
        throws Exception {

        mvc.perform(get("/health/liveness")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("status", is("UP")));
    }


}
