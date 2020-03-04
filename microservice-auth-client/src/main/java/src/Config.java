package src;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    //Allow us to pass token to the service we calling
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(ClientHttpRequestInterceptor userContextRestTemplateInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(userContextRestTemplateInterceptor);

        return restTemplate;
    }
}
