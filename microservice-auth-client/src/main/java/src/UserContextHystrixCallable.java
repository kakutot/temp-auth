package src;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

//Hystrix commands are executed in separate thread in THREAD mode =>
// we have to pass original thread data via such callable
public class UserContextHystrixCallable<T> implements Callable<T> {
    private final Callable<T> originalHystrixCallable;
    private UserContext userContext;

    public UserContextHystrixCallable(UserContext userContext, Callable<T> originalHystrixCallable) {
        this.userContext = userContext;
        this.originalHystrixCallable = originalHystrixCallable;
    }

    @Override
    public T call() throws Exception {
        UserContextHolder.setContext(userContext);
        this.userContext = null;

        return originalHystrixCallable.call();
    }
}
