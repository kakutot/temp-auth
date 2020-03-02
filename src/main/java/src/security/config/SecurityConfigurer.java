package src.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import src.security.OauthConsts;


@Configuration
@EnableWebSecurity
//@Order(1)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
             .csrf().disable()
        .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS,"/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
        .and()
                .httpBasic().realmName(OauthConsts.CRM_NAME);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("Roma").password(passwordEncoder.encode("ab")).roles("USER");
        /*auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery(
                        "SELECT username, password, enabled from user_secured where username = ?")
                .authoritiesByUsernameQuery(
                        "SELECT u.username, r.authority \n" +
                                "        FROM user_secured u, user_role r\n" +
                                "        WHERE u.id = r.user_id\n" +
                                "        AND u.username =?");*/
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean(name = "authenticationManagerBean")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
