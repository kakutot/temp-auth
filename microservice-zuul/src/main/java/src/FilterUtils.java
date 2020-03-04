package src;

import com.netflix.zuul.context.RequestContext;

import java.util.UUID;

public class FilterUtils {
    public final static String ZUUL_POST_FILTER = "post";
    public final static String ZUUL_PRE_FILTER = "pre";

    public static final String CORRELATION_ID = "correlation-id";
    public static final String AUTH_TOKEN = "Authorization";

    public static String getCorrelationId() {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        //First trying to check if header is present on original request
        final String correlationId = requestContext.getRequest().getHeader(CORRELATION_ID);
        if (correlationId != null) {
            return correlationId;
        } else {
            //Ask temporary in-memory map that will be merged after all filters applied
            return requestContext.getZuulRequestHeaders().get(CORRELATION_ID);
        }
    }

    public static void setCorrelationId(String correlationId) {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.getZuulRequestHeaders().put(CORRELATION_ID, correlationId);
    }

    public static void setCorrelationIdDefault() {
        setCorrelationId(UUID.randomUUID().toString());
    }

    public static String getAuthToken() {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        //Always ask only original request(auth token should be provided externally)
        return requestContext.getRequest().getHeader(AUTH_TOKEN);
    }

    public String getServiceId() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //We might not have a service id if we are using a static, non-eureka route.
        return ctx.get("serviceId") == null ? "" : ctx.get("serviceId").toString();
    }
}
