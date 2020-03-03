package src.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JWTConfigProperties {

    private String keyStore;
    private String keyStorePassword;
    private String keyPairAlias;
    private String keyPairPassword;
}
