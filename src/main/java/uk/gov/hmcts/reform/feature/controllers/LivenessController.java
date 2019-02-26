package uk.gov.hmcts.reform.feature.controllers;

import org.springframework.boot.actuate.health.Health;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class LivenessController {

    /**
     * Liveness endpoint to always return UP status and 200.
     *
     * @return health with UP status always.
     */
    @GetMapping(path = "/health/liveness", produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Health> getLiveness() {
        Health.Builder builder = new Health.Builder();
        Health health = builder.up().build();
        return ok(health);
    }
}
