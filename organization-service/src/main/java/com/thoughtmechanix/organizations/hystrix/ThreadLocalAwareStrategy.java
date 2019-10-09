package com.thoughtmechanix.organizations.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.thoughtmechanix.organizations.util.context.UserContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
* Hystrix allows you to define a (only 1) custom concurrency strategy that will wrap your Hystrix
* calls and allows you to inject any additional parent thread context into the threads
* managed by the Hystrix command
* */
public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {
    private HystrixConcurrencyStrategy existingStrategy;

    /*
    * Spring Cloud already has a concurrency class defined.
    * Pass the existing concurrency strategy into the class
    * constructor of your HystrixConcurrencyStrategy
    *
    * because
    * Spring Cloud already defines a HystrixConcurrencyStrategy, every method that
    * could be overridden needs to check whether an existing concurrency strategy is present and then either call the existing concurrency strategy’s method or the base Hystrix concurrency strategy method. You have to do this as a convention to ensure that
    * you properly invoke the already-existing Spring Cloud’s HystrixConcurrencyStrategy that deals with security. Otherwise, you can have nasty behav
    * */
    public ThreadLocalAwareStrategy(HystrixConcurrencyStrategy existingStrategy) {
        this.existingStrategy = existingStrategy;
    }

    /*
    * Inject your Callable
    * implementation that will
    * set the UserContext.
    * */
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return existingStrategy != null
                ? existingStrategy
                .wrapCallable(new DelegatingUserContextCallable<T>(callable, UserContextHolder.getContext()))
                : super.wrapCallable(new DelegatingUserContextCallable<T>(callable, UserContextHolder.getContext()));
    }

    /*
    * Several methods need to be
    * overridden. Either call the
    * existingConcurrencyStrategy method
    * implementation or call the base
    * HystrixConcurrencyStrategy
    * */

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return existingStrategy != null
                ? existingStrategy.getBlockingQueue(maxQueueSize)
                : super.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return existingStrategy != null
                ? existingStrategy.getRequestVariable(rv)
                : super.getRequestVariable(rv);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return existingStrategy != null
                ? existingStrategy.getThreadPool(threadPoolKey, corePoolSize,
                maximumPoolSize, keepAliveTime, unit, workQueue)
                : super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }
}
