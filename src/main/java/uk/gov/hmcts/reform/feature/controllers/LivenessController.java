package uk.gov.hmcts.reform.feature.controllers;

import org.springframework.boot.actuate.health.Health;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class LivenessController {

    /**
     * Liveness endpoint to always return UP status and 200.
     *
     * @return health with UP status always.
     */
    @RequestMapping(path = "/health/liveness", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Health> getLiveness() throws IOException {
        Health.Builder builder = new Health.Builder();
        Health health = builder.up().build();
        return ok(health);
    }
}
