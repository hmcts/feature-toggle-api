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
import uk.gov.hmcts.reform.feature.service.JwtParserService;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpMethod.GET;

public class IdamUserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtParserService parser;

    public IdamUserAuthenticationFilter(String pattern, JwtParserService parser) {
        super(new AntPathRequestMatcher(pattern, GET.name()));

        this.parser = parser;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        String authorisationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorisationHeader != null) {
            UserTokenDetails userTokenDetails = parser.parse(authorisationHeader);

            if (userTokenDetails != null) {
                return parseUserTokenDetails(authorisationHeader, userTokenDetails);
            }
        }

        // passing current auth in case some other authentication happened
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Authentication parseUserTokenDetails(String key, UserTokenDetails userTokenDetails) {
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
            AnonymousAuthenticationToken.class
        );
    }
}
