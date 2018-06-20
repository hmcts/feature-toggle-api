package uk.gov.hmcts.reform.feature.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.gov.hmcts.reform.feature.model.UserTokenDetails;

import java.io.IOException;
import java.util.List;
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
        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();

        if (authorisationHeader != null) {
            UserTokenDetails userTokenDetails = null;

            if (userTokenDetails != null) {
                return parseUserTokenDetails(authorisationHeader, userTokenDetails, originalAuth);
            }
        }

        // passing current auth in case some other authentication happened
        return originalAuth;
    }

    private Authentication parseUserTokenDetails(String key,
                                                 UserTokenDetails userTokenDetails,
                                                 Authentication originalAuth) {
        // use of combination of `.roles` and `.authorities` overrides each other
        // everything gets converted to authorities
        // roles are prefixed
        List<GrantedAuthority> authorities = userTokenDetails.getRoles();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + Roles.USER));

        UserDetails details = User.withUsername("idam-user-" + userTokenDetails.getId())
            .password("")
            .authorities(authorities)
            .build();

        return new RunAsUserToken(
            key,
            details,
            details.getPassword(),
            details.getAuthorities(),
            originalAuth == null ? AnonymousAuthenticationToken.class : originalAuth.getClass()
        );
    }
}
