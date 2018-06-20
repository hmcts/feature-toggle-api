package uk.gov.hmcts.reform.feature.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

import java.util.Arrays;
import java.util.List;

public class JwtParserService {

    private static final Logger log = LoggerFactory.getLogger(JwtParserService.class);

    private final JwtParser parser;

    public JwtParserService(JwtParser parser) {
        this.parser = parser;
    }

    public UserTokenDetails parse(String jwtToken) {
        try {
            Claims claims = parser.parseClaimsJws(jwtToken).getBody();

            String id = claims.get("id").toString();
            List<String> roles = Arrays.asList(claims.get("data").toString().split(","));

            return new UserTokenDetails(id, roles);
        } catch (Exception exception) {
            log.warn(exception.getMessage(), exception);

            return null;
        }
    }
}
