package uk.gov.hmcts.reform.feature.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.reform.feature.Application;

@ComponentScan(basePackages = {
    "org.ff4j.services",
    "org.ff4j.spring.boot.web.api.resources"
})
@Configuration
@EnableSwagger2
@Lazy
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(Predicates.or(
                RequestHandlerSelectors.basePackage(Application.class.getPackage().getName() + ".controllers"),
                RequestHandlerSelectors.basePackage("org.ff4j.spring.boot.web.api.resources")
            ))
            .paths(PathSelectors.any())
            .build();
    }

}
