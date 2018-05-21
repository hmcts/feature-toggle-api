package uk.gov.hmcts.reform.feature.config;

import org.ff4j.FF4j;
import org.ff4j.web.FF4jDispatcherServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.feature.FeatureProvider;

import java.util.Collections;

import static org.ff4j.web.bean.WebConstants.SERVLETPARAM_FF4JPROVIDER;

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

    @Bean
    public ServletRegistrationBean ff4jServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(
            new FF4jDispatcherServlet(),
            "/ff4j-console/*"
        );

        bean.setName("ff4j-console");
        bean.setLoadOnStartup(1);
        bean.setInitParameters(Collections.singletonMap(
            SERVLETPARAM_FF4JPROVIDER, FeatureProvider.class.getCanonicalName()
        ));

        return bean;
    }
}
