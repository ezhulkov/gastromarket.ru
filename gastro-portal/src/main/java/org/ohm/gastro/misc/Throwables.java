package org.ohm.gastro.misc;

import java.util.concurrent.Callable;

public class Throwables {

    public interface ExceptionWrapper<E> {
        E wrap(Exception e);
    }

    public interface ThrowableRunnable {
        void run() throws Exception;
    }

    public static <T> T propagate(Callable<T> callable) throws RuntimeException {
        return propagate(callable, RuntimeException::new);
    }

    public static void propagate(ThrowableRunnable runnable) throws RuntimeException {
        propagate(runnable, RuntimeException::new);
    }

    public static <T, E extends Throwable> T propagate(Callable<T> callable, ExceptionWrapper<E> wrapper) throws E {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw wrapper.wrap(e);
        }
    }

    public static <E extends Throwable> void propagate(ThrowableRunnable runnable, ExceptionWrapper<E> wrapper) throws E {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw wrapper.wrap(e);
        }
    }

}