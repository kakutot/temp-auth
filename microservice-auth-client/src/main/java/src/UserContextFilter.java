package src;

import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserContextFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authToken = request.getHeader(UserContext.AUTH_TOKEN);
        if (authToken != null) {
            UserContextHolder.getUserContext().setAuthToken(authToken);
        }
        String correlationId = request.getHeader(UserContext.CORRELATION_ID);
        if (correlationId != null) {
            UserContextHolder.getUserContext().setCorrelationId(correlationId);
        }
        chain.doFilter(request, response);
    }
}
