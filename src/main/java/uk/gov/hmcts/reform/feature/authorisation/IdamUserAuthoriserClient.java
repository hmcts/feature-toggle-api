package uk.gov.hmcts.reform.feature.authorisation;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class IdamUserAuthoriserClient implements IdamUserAuthoriser {

    private final String idamUrl;
    private final RestTemplate restTemplate;

    public IdamUserAuthoriserClient(String idamUrl) {
        this.idamUrl = idamUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public UserTokenDetails getUserDetails(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, authHeader);

        return restTemplate.exchange(
            idamUrl + "/details",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            UserTokenDetails.class
        ).getBody();
    }
}
