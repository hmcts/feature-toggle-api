package uk.gov.hmcts.reform.feature;

import org.ff4j.FF4j;
import org.ff4j.web.FF4jProvider;

public class FeatureProvider implements FF4jProvider {

    private final FF4j ff4j;

    public FeatureProvider() {
        this.ff4j = new FF4j();
    }

    @Override
    public FF4j getFF4j() {
        return ff4j;
    }
}
