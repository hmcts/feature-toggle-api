package uk.gov.hmcts.reform.feature.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.feature.exception.UnauthenticatedException;

@Service
public class AuthService {

    public static final String SERVICE_AUTH_HEADER = "ServiceAuthorization";

    private final AuthTokenValidator authTokenValidator;

    public AuthService(AuthTokenValidator authTokenValidator) {
        this.authTokenValidator = authTokenValidator;
    }

    public String authenticate(String authHeader) {
        if (authHeader == null) {
            throw new UnauthenticatedException("Missing " + SERVICE_AUTH_HEADER + " header");
        } else {
            return authTokenValidator.getServiceName(authHeader);
        }
    }
}
