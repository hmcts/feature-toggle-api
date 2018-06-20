package uk.gov.hmcts.reform.feature.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtParserServiceTest {

    private static final String SIGNING_KEY = "signing";

    private final JwtParserService service = new JwtParserService(Jwts.parser());

    @Test
    public void should_parse_valid_jwt_token_and_get_user_details() {
        // given
        String jwtToken = Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
            .addClaims(ImmutableMap.of("id", "1234", "data", "beta,test"))
            .compact();

        // then
        assertThat(service.parse(jwtToken)).isEqualToComparingFieldByFieldRecursively(
            new UserTokenDetails("1234", ImmutableList.of("beta", "test"))
        );
    }

    @Test
    public void should_parse_jwt_token_when_different_signing_key_is_used() {
        // given
        String jwtToken = Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY + "key")
            .addClaims(ImmutableMap.of("id", "1234", "data", "beta,test"))
            .compact();

        // then
        assertThat(service.parse(jwtToken)).isEqualToComparingFieldByFieldRecursively(
            new UserTokenDetails("1234", ImmutableList.of("beta", "test"))
        );
    }

    @Test
    public void should_fail_to_parse_jwt_token_when_wrong_jwt_string_passed_and_return_null() {
        assertThat(service.parse("jtwtoken.with2dots.init")).isNull();
    }

    @Test
    public void should_fail_to_parse_jwt_token_when_wrong_string_passed_and_return_null() {
        assertThat(service.parse("some random string")).isNull();
    }
}
