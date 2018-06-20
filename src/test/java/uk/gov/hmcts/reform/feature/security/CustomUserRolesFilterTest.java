package uk.gov.hmcts.reform.feature.security;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserRolesFilterTest {

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private CustomUserRolesFilter filter;

    @Before
    public void setUp() {
        filter = new CustomUserRolesFilter("/**");
    }

    @Test
    public void should_return_authorisation_from_context_when_no_headers_passed()
        throws IOException, ServletException {
        // given
        Authentication anonymous = getAnonymous();
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isEqualToComparingFieldByFieldRecursively(anonymous);
    }

    @Test
    public void should_return_authorisation_from_context_when_header_value_is_empty()
        throws IOException, ServletException {
        // given
        Authentication anonymous = getAnonymous();
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        // and
        given(request.getHeader(CustomUserRolesFilter.USER_ID_HEADER)).willReturn("");
        given(request.getHeader(CustomUserRolesFilter.USER_ROLES_HEADER)).willReturn("");

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isEqualToComparingFieldByFieldRecursively(anonymous);
    }

    @Test
    public void should_return_run_as_user_token_when_headers_are_provided() throws IOException, ServletException {
        // given
        given(request.getHeader(CustomUserRolesFilter.USER_ID_HEADER)).willReturn("id");
        given(request.getHeader(CustomUserRolesFilter.USER_ROLES_HEADER)).willReturn("test, beta");

        // when
        Authentication filteredAuth = filter.attemptAuthentication(request, response);

        // then
        assertThat(filteredAuth).isInstanceOf(RunAsUserToken.class);
        assertThat(filteredAuth.getAuthorities())
            .extracting("authority")
            .hasSameElementsAs(ImmutableList.of("test", "beta", "ROLE_" + Roles.USER));
    }

    private Authentication getAnonymous() {
        return new AnonymousAuthenticationToken(
            "anonymous",
            User.withUsername("anonymous").password("").roles(Roles.USER).build(),
            ImmutableList.of(new SimpleGrantedAuthority(Roles.USER))
        );
    }
}
