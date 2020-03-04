package src;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ResConfig {

    @Bean
    @LoadBalanced
    @Qualifier(value = "lbalanced")
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
