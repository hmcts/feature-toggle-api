package uk.gov.hmcts.reform.feature.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String errorPage;

        try (InputStream in = getClass().getResourceAsStream("/error.html")) {
            errorPage = StreamUtils.copyToString(in, Charset.defaultCharset())
                .replace("{url}", request.getRequestURI())
                .replace("{message}", accessDeniedException.getMessage());
        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setContentType(TEXT_HTML_VALUE);
            outputStream.write(errorPage.getBytes());
        }
    }
}
