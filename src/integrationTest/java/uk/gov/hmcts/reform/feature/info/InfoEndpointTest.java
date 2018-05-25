package uk.gov.hmcts.reform.feature.info;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_info_details_of_app_as_per_configuration() throws Exception {
        this.mockMvc.perform(get("/info"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.app.version")
                .value("1.0.0"))
            .andExpect(jsonPath("$.app.description")
                .value("This application allows to enable/disable features at runtime without deployment."))
            .andExpect(jsonPath("$.app.name")
                .value("Feature Toggle API"));
    }
}
