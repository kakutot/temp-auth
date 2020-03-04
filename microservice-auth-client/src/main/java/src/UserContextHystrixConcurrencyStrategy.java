package src;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserContextHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private final HystrixConcurrencyStrategy springHystrixConcurrencyStrategy;

    public UserContextHystrixConcurrencyStrategy(HystrixConcurrencyStrategy springHystrixConcurrencyStrategy) {
        this.springHystrixConcurrencyStrategy = springHystrixConcurrencyStrategy;
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        ThreadPoolExecutor threadPoolExecutor;

        if (springHystrixConcurrencyStrategy != null) {
            threadPoolExecutor = springHystrixConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        } else {
            threadPoolExecutor = super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        return threadPoolExecutor;
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
        ThreadPoolExecutor threadPoolExecutor;

        if (springHystrixConcurrencyStrategy != null) {
            threadPoolExecutor = springHystrixConcurrencyStrategy.getThreadPool(threadPoolKey, threadPoolProperties);
        } else {
            threadPoolExecutor = super.getThreadPool(threadPoolKey, threadPoolProperties);
        }

        return threadPoolExecutor;
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        BlockingQueue<Runnable> blockingQueue;

        if (springHystrixConcurrencyStrategy != null) {
            blockingQueue = springHystrixConcurrencyStrategy.getBlockingQueue(maxQueueSize);
        } else {
            blockingQueue = super.getBlockingQueue(maxQueueSize);
        }

        return blockingQueue;
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        final Callable userContextHystrixCallable = new UserContextHystrixCallable(UserContextHolder.getUserContext(), callable);
        Callable<T> wrappedCallable;

        if (springHystrixConcurrencyStrategy != null) {
            wrappedCallable = springHystrixConcurrencyStrategy.wrapCallable(callable);
        } else {
            wrappedCallable = super.wrapCallable(userContextHystrixCallable);
        }

        return wrappedCallable;
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        HystrixRequestVariable<T> hystrixRequestVariable;

        if (springHystrixConcurrencyStrategy != null) {
            hystrixRequestVariable = springHystrixConcurrencyStrategy.getRequestVariable(rv);
        } else {
            hystrixRequestVariable = super.getRequestVariable(rv);
        }

        return hystrixRequestVariable;
    }
}
