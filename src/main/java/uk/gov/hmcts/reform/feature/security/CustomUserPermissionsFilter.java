package uk.gov.hmcts.reform.feature.security;

import com.google.common.base.Strings;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;
import uk.gov.hmcts.reform.feature.model.UserRoles;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpMethod.GET;

public class CustomUserPermissionsFilter extends GenericFilterBean {

    public static final String USER_ID_HEADER = "X-USER-ID";

    public static final String USER_PERMISSIONS_HEADER = "X-USER-PERMISSIONS";

    private final RequestMatcher matcher;

    public CustomUserPermissionsFilter(String pattern) {
        matcher = new AntPathRequestMatcher(pattern, GET.name());
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (matcher.matches(httpRequest)) {
            String userIdHeader = httpRequest.getHeader(USER_ID_HEADER);
            String userRolesHeader = httpRequest.getHeader(USER_PERMISSIONS_HEADER);
            Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();

            if (checkHeadersAreValid(userIdHeader, userRolesHeader)) {
                UserRoles userRoles = new UserRoles(
                    userIdHeader,
                    userRolesHeader.split(",")
                );

                Authentication runAsUser = parseUserRoles(userRoles, originalAuth);

                SecurityContextHolder.getContext().setAuthentication(runAsUser);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean checkHeadersAreValid(String userIdHeader, String userRolesHeader) {
        return !Strings.isNullOrEmpty(userIdHeader) && ! Strings.isNullOrEmpty(userRolesHeader);
    }

    private Authentication parseUserRoles(UserRoles userRoles, Authentication originalAuth) {
        // prefixing external users to separate out from integrated ones.
        // usernames are used in ff4j monitoring tool
        UserDetails details = User.withUsername("external:" + userRoles.getId())
            .password("")
            .authorities(userRoles.getAuthorities())
            .build();

        return new RunAsUserToken(
            userRoles.getId(),
            details,
            details.getPassword(),
            details.getAuthorities(),
            originalAuth == null ? AnonymousAuthenticationToken.class : originalAuth.getClass()
        );
    }
}
