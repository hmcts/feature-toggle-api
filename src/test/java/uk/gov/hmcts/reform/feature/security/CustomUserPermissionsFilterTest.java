package uk.gov.hmcts.reform.feature.security;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserPermissionsFilterTest {

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain chain;

    private CustomUserPermissionsFilter filter;

    @Before
    public void setUp() {
        filter = new CustomUserPermissionsFilter("/**");
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void should_return_authorisation_from_context_when_no_headers_passed()
        throws IOException, ServletException {
        // given
        Authentication anonymous = new AnonymousAuthenticationToken(
            "anonymous",
            User.withUsername("anonymous").password("").roles(Roles.USER).build(),
            ImmutableList.of(new SimpleGrantedAuthority(Roles.USER))
        );
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        // when
        filter.doFilter(request, response, chain);

        // then
        Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(updatedAuth).isEqualToComparingFieldByFieldRecursively(anonymous);
    }

    @Test
    public void should_return_authorisation_from_context_when_header_value_is_empty()
        throws IOException, ServletException {
        // given
        Authentication testUser = getTestingUser();
        SecurityContextHolder.getContext().setAuthentication(testUser);

        // and
        given(request.getHeader(CustomUserPermissionsFilter.USER_ID_HEADER)).willReturn("");
        given(request.getHeader(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER)).willReturn("");

        // when
        filter.doFilter(request, response, chain);

        // then
        Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(updatedAuth).isEqualToComparingFieldByFieldRecursively(testUser);
    }

    @Test
    public void should_return_run_as_user_token_when_headers_are_provided() throws IOException, ServletException {
        // given
        given(request.getHeader(CustomUserPermissionsFilter.USER_ID_HEADER)).willReturn("id");
        given(request.getHeader(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER)).willReturn("test, beta");

        // when
        filter.doFilter(request, response, chain);

        // then
        Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(updatedAuth).isInstanceOf(RunAsUserToken.class);
        assertThat(((RunAsUserToken) updatedAuth).getOriginalAuthentication())
            .isSameAs(AnonymousAuthenticationToken.class);
        assertThat(updatedAuth.getAuthorities())
            .extracting("authority")
            .hasSameElementsAs(ImmutableList.of("test", "beta", "ROLE_" + Roles.USER));
    }

    @Test
    public void should_return_run_as_user_token_with_correct_original_authentication()
        throws IOException, ServletException {
        // given
        Authentication testUser = getTestingUser();
        SecurityContextHolder.getContext().setAuthentication(testUser);

        // and
        given(request.getHeader(CustomUserPermissionsFilter.USER_ID_HEADER)).willReturn("id");
        given(request.getHeader(CustomUserPermissionsFilter.USER_PERMISSIONS_HEADER)).willReturn("test");

        // when
        filter.doFilter(request, response, chain);

        // then
        Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(updatedAuth).isInstanceOf(RunAsUserToken.class);
        assertThat(((RunAsUserToken) updatedAuth).getOriginalAuthentication()).isSameAs(testUser.getClass());
        assertThat(updatedAuth.getAuthorities())
            .extracting("authority")
            .hasSameElementsAs(ImmutableList.of("test", "ROLE_" + Roles.USER));
    }

    private Authentication getTestingUser() {
        return new TestingAuthenticationToken(
            User.withUsername("test").password("").authorities("test").build(), ""
        );
    }
}
