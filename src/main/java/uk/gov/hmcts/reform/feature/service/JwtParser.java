package uk.gov.hmcts.reform.feature.service;

import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

public interface JwtParser {

    UserTokenDetails parse(String jwtToken);
}
