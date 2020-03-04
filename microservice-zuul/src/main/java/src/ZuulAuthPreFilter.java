package src;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ZuulAuthPreFilter extends ZuulFilter {
    @Autowired
    @Qualifier(value = "lbalanced")
    private RestTemplate restTemplate;

    @Override
    public String filterType() {
        return FilterUtils.ZUUL_PRE_FILTER.toString();
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        if (requestContext.getRequest().getRequestURI().contains("/authservice/oauth/token")){
            return null;
        }
        final String bearerPrefix = "Bearer";
        if (FilterUtils.getAuthToken() == null || !FilterUtils.getAuthToken().contains(bearerPrefix)) {
            setUnauthorizedResponse(requestContext);
        } else if (FilterUtils.getAuthToken().contains(bearerPrefix)) {
            String hexedJWT = FilterUtils.getAuthToken().replace("Bearer ", "");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(hexedJWT);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> isTokenRevoked = restTemplate.exchange("http://authservice/revoke-token/{hexedJWT}", HttpMethod.GET,
                    entity, String.class, hexedJWT);
            log.info("Is token revoked {}", isTokenRevoked.getBody());
            if (isTokenRevoked.getBody() != null && Boolean.parseBoolean(isTokenRevoked.getBody())) {
              setUnauthorizedResponse(requestContext);
            }
        }

        return null;
    }

    private void setUnauthorizedResponse(RequestContext requestContext) {
        //requestContext.unset();
        requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().addHeader("Token-status", "revoked");
    }

}
