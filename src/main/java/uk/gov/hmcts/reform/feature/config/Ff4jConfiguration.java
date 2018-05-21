package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Ff4jConfiguration {

    @Bean
    public FF4j getFF4j() {
        return new FF4j();
    }
}
