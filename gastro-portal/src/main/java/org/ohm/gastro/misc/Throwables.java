package org.ohm.gastro.misc;

import java.util.concurrent.Callable;

public class Throwables {

    public static interface ExceptionWrapper<E> {
        E wrap(Exception e);
    }

    public static <T> T propagate(Callable<T> callable) throws RuntimeException {
        return propagate(callable, RuntimeException::new);
    }

    public static void propagate(RunnableTh runnable) throws RuntimeException {
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

    public static <E extends Throwable> void propagate(RunnableTh runnable, ExceptionWrapper<E> wrapper) throws E {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw wrapper.wrap(e);
        }
    }

}