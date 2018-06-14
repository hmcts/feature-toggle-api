package uk.gov.hmcts.reform.feature.authorisation;

import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

public interface IdamUserAuthoriser {

    UserTokenDetails getUserDetails(String authHeader);
}
