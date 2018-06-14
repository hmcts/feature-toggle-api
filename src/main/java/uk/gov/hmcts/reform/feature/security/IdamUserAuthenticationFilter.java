package uk.gov.hmcts.reform.feature.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.gov.hmcts.reform.feature.authorisation.IdamUserAuthoriser;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpMethod.GET;

public class IdamUserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private IdamUserAuthoriser idamAuthoriser;

    public IdamUserAuthenticationFilter(String pattern) {
        super(new AntPathRequestMatcher(pattern, GET.name()));
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException, IOException, ServletException {
        return null;
    }
}
