package uk.gov.hmcts.reform.feature.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(authority ->
            // since roles are created with automatic prefix of `ROLE_` - authorities come in raw
            // need to strip the prefix to match successfully
            authority.getAuthority().replaceFirst("ROLE_", "").equals(Roles.ROLE_ADMIN)
        );
        String targetUrl = isAdmin ? "/ff4j-web-console/home" : "/?login";

        response.sendRedirect(response.encodeRedirectURL(targetUrl));
    }
}
