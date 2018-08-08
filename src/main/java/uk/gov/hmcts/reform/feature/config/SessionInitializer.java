package uk.gov.hmcts.reform.feature.config;

/**
 * This class is used to register jdbc session with spring filter
 */

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class SessionInitializer extends AbstractHttpSessionApplicationInitializer {

    public SessionInitializer() {
        super(SessionConfiguration.class);
    }
}
