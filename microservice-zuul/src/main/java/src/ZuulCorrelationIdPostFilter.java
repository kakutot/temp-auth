package src;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static src.FilterUtils.ZUUL_POST_FILTER;

@Component
@Slf4j
public class ZuulCorrelationIdPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return ZUUL_POST_FILTER;
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
      RequestContext requestContext = RequestContext.getCurrentContext();
      String corrId = FilterUtils.getCorrelationId();
      log.info("Token : {}", FilterUtils.getAuthToken().replace("Bearer ", ""));
      log.info("Adding correlation id : {}", corrId);
      requestContext.getResponse().addHeader(FilterUtils.CORRELATION_ID, corrId);
      log.info("Processing request is done : {}", RequestContext.getCurrentContext().getRequest().getRequestURI());
      return null;
    }
}
