package uk.gov.hmcts.reform.feature.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@AutoConfigureAfter(FlywayAutoConfiguration.class)
@DependsOn({"flyway", "flywayInitializer"})
public class FlywayIntegrationConfig {
}
