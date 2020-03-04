package src;

import java.util.Objects;

public class UserContextHolder {
    private final static ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

    public static UserContext getUserContext() {
        if (userContextThreadLocal.get() == null) {
            userContextThreadLocal.set(new UserContext());
        }
        return userContextThreadLocal.get();
    }

    public static final void setContext(UserContext context) {
        Objects.requireNonNull(context, "Only non-null UserContext instances are permitted");
        userContextThreadLocal.set(context);
    }

}
