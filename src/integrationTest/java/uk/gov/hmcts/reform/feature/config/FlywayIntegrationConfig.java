package uk.gov.hmcts.reform.feature.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter(FlywayAutoConfiguration.class)
@ConditionalOnProperty(prefix = "flyway", name = "enabled", matchIfMissing = true)
@DependsOn({"flyway", "flywayInitializer"})
public class FlywayIntegrationConfig {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void loadSqlData() {
        //Clear data-Postconstructs are executed twice.Can be removed once we migrate to Spring boot 2.0.2 RELEASE
        executeSql("delete-exisiting-data.sql");

        //load data
        executeSql("initial-user-roles-load.sql");
    }

    private void executeSql(String sqlFile) {
        ScriptUtils.executeSqlScript(
            DataSourceUtils.getConnection(dataSource),
            new ClassPathResource(sqlFile));
    }
}
