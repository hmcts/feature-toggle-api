package uk.gov.hmcts.reform.feature.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpMethod.GET;

public class IdamUserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public IdamUserAuthenticationFilter(String pattern) {
        super(new AntPathRequestMatcher(pattern, GET.name()));
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        String authorisationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorisationHeader != null) {
            UserTokenDetails userTokenDetails = null;// todo parse jwt

            if (userTokenDetails != null) {
                return parseUserTokenDetails(authorisationHeader, userTokenDetails);
            }
        }

        // passing current auth in case some other authentication happened
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Authentication parseUserTokenDetails(String key, UserTokenDetails userTokenDetails) {
        UserDetails details = User.withUsername("idam-user-" + userTokenDetails.id)
            .password("")
            .roles(Roles.USER) // default built-in role for any user
            // spring security clashes all roles and authorities into one
            // `userTokenDetails.roles` come back from idam and can be in any `authority` format
            // hence assigning as authorities for spring security to understand access levels correctly
            .authorities(userTokenDetails.roles)
            .build();

        return new RunAsUserToken(
            key,
            details,
            details.getPassword(),
            details.getAuthorities(),
            AnonymousAuthenticationToken.class
        );
    }
}
