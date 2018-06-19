package uk.gov.hmcts.reform.feature.security;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;
import uk.gov.hmcts.reform.feature.service.JwtParserService;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class IdamUserAuthenticationFilterTest {

    @Mock
    private JwtParserService parser;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private IdamUserAuthenticationFilter filter;

    @Before
    public void setUp() {
        filter = new IdamUserAuthenticationFilter("/**", parser);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void should_return_authorisation_from_context_when_no_header_passed() throws IOException, ServletException {
        // given
        Authentication anonymous = new AnonymousAuthenticationToken(
            "anonymous",
            User.withUsername("anonymous").password("").roles(Roles.USER).build(),
            ImmutableList.of(new SimpleGrantedAuthority(Roles.USER))
        );
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isEqualToComparingFieldByFieldRecursively(anonymous);
    }

    @Test
    public void should_return_authorisation_from_context_when_jwt_parser_returns_null()
        throws IOException, ServletException {
        // given
        Authentication testingUser = getTestingUser();
        SecurityContextHolder.getContext().setAuthentication(testingUser);

        // and
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("value");

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isEqualToComparingFieldByFieldRecursively(testingUser);
    }

    @Test
    public void should_return_run_as_user_token_when_jwt_parser_returns_user_details()
        throws IOException, ServletException {
        // given
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("some_value");
        given(parser.parse("some_value")).willReturn(new UserTokenDetails("id", ImmutableList.of("test")));

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isInstanceOf(RunAsUserToken.class);
        assertThat(((RunAsUserToken) filteredAuth).getOriginalAuthentication())
            .isSameAs(AnonymousAuthenticationToken.class);
        assertThat(filteredAuth.getAuthorities())
            .extracting("authority")
            .hasSameElementsAs(ImmutableList.of("test", "ROLE_" + Roles.USER));
    }

    @Test
    public void should_return_run_as_user_token_with_correct_original_authentication()
        throws IOException, ServletException {
        // given
        Authentication testingUser = getTestingUser();
        SecurityContextHolder.getContext().setAuthentication(testingUser);

        // and
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("some_value");
        given(parser.parse("some_value")).willReturn(new UserTokenDetails("id", ImmutableList.of("test")));

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isInstanceOf(RunAsUserToken.class);
        assertThat(((RunAsUserToken) filteredAuth).getOriginalAuthentication()).isSameAs(testingUser.getClass());
        assertThat(filteredAuth.getAuthorities())
            .extracting("authority")
            .hasSameElementsAs(ImmutableList.of("test", "ROLE_" + Roles.USER));
    }

    private Authentication getTestingUser() {
        return new TestingAuthenticationToken(
            User.withUsername("test").password("").authorities("test").build(), ""
        );
    }
}
