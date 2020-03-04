package src;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sun.research.ws.wadl.HTTPMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableResourceServer
@EnableEurekaClient
@RestController
@EnableCircuitBreaker
@Slf4j
public class App {

    //Rest template that adds Authorization header to requests
    @Autowired private OAuth2RestTemplate oAuth2RestTemplate;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    //Pseudo remote call => separate thread pool
    @GetMapping(value = "/test/hystrix", produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = "6600"),
            //Test execution window
            @HystrixProperty(
                    name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
            //Test execution number of buckets(where info stored) metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0
            @HystrixProperty(
                    name = "metrics.rollingStats.numBuckets", value = "5"),
            //Number of requests before calls are aborted
            @HystrixProperty(
                    name = "circuitBreaker.requestVolumeThreshold",
                    value = "10"),
            //Percentage of requests that should fail to abort calls
            @HystrixProperty(
                    name = "circuitBreaker.errorThresholdPercentage",
                    value = "75"),
            //Time after Hystrix will try to call this method again
            @HystrixProperty(
                    name = "circuitBreaker.sleepWindowInMilliseconds",
                    value = "6000")
    }, fallbackMethod = "hystrixFallback",
       threadPoolKey = "defaultKey",
       threadPoolProperties = {
               @HystrixProperty(name = "coreSize", value = "2"),
               @HystrixProperty(name = "maxQueueSize", value = "1"),
       })
    public List<Product> hystrixTest() {
        log.info(UserContext.CORRELATION_ID + "=" + UserContextHolder.getUserContext().getCorrelationId());
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextInt = random.nextInt(4);
        if(nextInt == 2) {
            log.error("Bad number");
            try {
                Thread.sleep(11000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Product product = oAuth2RestTemplate.exchange("http://localhost:5555/api/productService/product/{prodId}",
                                            HttpMethod.GET,
                                            null,
                                            Product.class,
                                            123)
                .getBody();
        return Collections.singletonList(product);
    }

    public List<Product> hystrixFallback() {
        return Collections.singletonList(new Product(-1, "default", ProductType.VEG));
    }

   static class Product {
        public long id;
        public java.lang.String name;
        public ProductType productType;

        public Product(long id, String name, ProductType productType) {
            this.id = id;
            this.name = name;
            this.productType = productType;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ProductType getProductType() {
            return productType;
        }

        public void setProductType(ProductType productType) {
            this.productType = productType;
        }
    }

    public enum ProductType {
        VEG, FRUIT;
    }
}
