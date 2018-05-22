package uk.gov.hmcts.reform.feature.config;

import org.ff4j.spring.boot.autoconfigure.FF4JConfiguration;
import org.ff4j.web.FF4jDispatcherServlet;
import org.ff4j.web.embedded.ConsoleServlet;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ConsoleServlet.class, FF4jDispatcherServlet.class})
@AutoConfigureAfter(FF4JConfiguration.class)
public class FF4jServletConfig extends SpringBootServletInitializer {

    @Bean
    public ServletRegistrationBean ff4jDispatcherServletRegistrationBean(FF4jDispatcherServlet ff4jDispatcherServlet) {
        return new ServletRegistrationBean(ff4jDispatcherServlet, "/ff4j-web-console/*");
    }
}
