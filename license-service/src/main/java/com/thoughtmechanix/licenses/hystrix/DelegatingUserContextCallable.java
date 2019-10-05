package com.thoughtmechanix.licenses.hystrix;

import com.thoughtmechanix.licenses.model.UserContext;
import com.thoughtmechanix.licenses.util.context.UserContextHolder;

import java.util.concurrent.Callable;

public class DelegatingUserContextCallable<V> implements Callable<V> {
    private final Callable<V> delegate;
    private UserContext originalUserContext;

    /*
    * Custom Callable class will be
    * passed the original Callable
    * class that will invoke your
    * Hystrix protected code and
    * UserContext coming in from
    * the parent thread
    * */
    public DelegatingUserContextCallable(Callable<V> delegate, UserContext userContext) {
        this.delegate = delegate;
        this.originalUserContext = userContext;
    }

    /*
    * The call() function
    * is invoked before
    * the method
    * protected by the
    * @HystrixCommand
    * annotation.
    * */
    @Override
    public V call() throws Exception {
        UserContextHolder.setContext(originalUserContext);
        try {
            return delegate.call();
        } finally {
            this.originalUserContext = null;
        }
    }
}
