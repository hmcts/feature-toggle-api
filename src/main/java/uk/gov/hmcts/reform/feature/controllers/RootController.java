package uk.gov.hmcts.reform.feature.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Default endpoints per application.
 */
@RestController
public class RootController {

    static final String TITLE = "Welcome to Feature Toggle API";

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
     * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
     * @return Welcome message from the service.
     */
    @GetMapping
    public ResponseEntity<String> welcome() throws URISyntaxException, IOException {
        String response = TITLE;
        URL url = getClass().getClassLoader().getResource("index.html");

        if (url != null) {
            Path path = Paths.get(url.toURI());
            StringBuilder builder = new StringBuilder();

            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(builder::append);
            }

            response = builder.toString().replace("{title}", TITLE);
        }

        return ok(response);
    }
}
