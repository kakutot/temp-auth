package src.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(JWTConfigProperties.class)
public class JWTConfig {

    private final AuthenticationManager authenticationManager;
    private final JWTConfigProperties jwtConfigProperties;

    @Autowired
    public JWTConfig(AuthenticationManager authenticationManager,
                     JWTConfigProperties jwtConfigProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtConfigProperties = jwtConfigProperties;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        //defaultTokenServices.setClientDetailsService(new JdbcClientDetailsService(dataSource));
        defaultTokenServices.setAuthenticationManager(authenticationManager);

        return defaultTokenServices;
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        /*
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtKey);
        return converter;*/
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource(jwtConfigProperties.getKeyStore()),
                jwtConfigProperties.getKeyStorePassword().toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(
                jwtConfigProperties.getKeyPairAlias(),
                jwtConfigProperties.getKeyPairPassword().toCharArray()));
        return converter;
    }
}