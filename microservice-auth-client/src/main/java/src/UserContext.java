package src;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserContext {
    public static final String CORRELATION_ID = "correlation-id";
    public static final String AUTH_TOKEN = "Authorization";
    public static final String USER_ID = "user-id";

    @Getter private String correlationId;
    @Getter private String authToken;

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        log.info(CORRELATION_ID + "=" + correlationId);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        log.info(AUTH_TOKEN + "=" + AUTH_TOKEN);
    }
}