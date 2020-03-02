package src.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import src.security.ClientScopes;

@Configuration
//The annotation will basically create another WebSecurityConfigurerAdapters with hard-coded order of 3
//from this bean
@EnableResourceServer
public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2AccessDeniedHandler auth2AccessDeniedHandler;

    private static final String SECURED_USER_CLIENT = "#oauth2.hasScope('"+ ClientScopes.USER_INFO + "')";

    private static final String SECURED_PATTERN_USER = "/user";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(SECURED_PATTERN_USER)
                    .access(SECURED_USER_CLIENT)
                .anyRequest()
                    .authenticated();
        //.and()
             //.exceptionHandling().accessDeniedHandler(auth2AccessDeniedHandler);
    }
}