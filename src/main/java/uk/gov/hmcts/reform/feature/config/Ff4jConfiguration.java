package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(value = {
    "org.ff4j.spring.boot.web.api",
    "org.ff4j.services",
    "org.ff4j.aop",
    "org.ff4j.spring"
})
@ConditionalOnClass(FF4j.class)
@Configuration
public class Ff4jConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FF4j getFF4j() {
        return new FF4j();
    }
}
