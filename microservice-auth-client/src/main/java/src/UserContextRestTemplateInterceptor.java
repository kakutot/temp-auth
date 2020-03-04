package src;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserContextRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(UserContext.AUTH_TOKEN, UserContextHolder.getUserContext().getAuthToken());
        request.getHeaders().add(UserContext.CORRELATION_ID, UserContextHolder.getUserContext().getCorrelationId());

        return execution.execute(request, body);
    }
}
