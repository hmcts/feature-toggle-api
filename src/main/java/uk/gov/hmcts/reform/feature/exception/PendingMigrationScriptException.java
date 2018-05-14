package uk.gov.hmcts.reform.feature.exception;

public class PendingMigrationScriptException extends RuntimeException {

    public PendingMigrationScriptException(String script) {
        super("Found migration not yet applied " + script);
    }
}
