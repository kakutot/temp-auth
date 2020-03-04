package src;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZuulCorrelationIdPreFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterUtils.ZUUL_PRE_FILTER;
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
        String corrId;
        if ((corrId = FilterUtils.getCorrelationId()) != null) {
            log.info("Correlation id was present on initial request : {}", corrId);
        } else {
            FilterUtils.setCorrelationIdDefault();
            corrId = FilterUtils.getCorrelationId();
            log.info("Correlation id was generated : {}", corrId);
        }
        log.info("Processing request started : {}", RequestContext.getCurrentContext().getRequest().getRequestURI());
        return null;
    }
}
