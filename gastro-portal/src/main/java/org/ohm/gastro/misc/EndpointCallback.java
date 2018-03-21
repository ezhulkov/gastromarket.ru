package org.ohm.gastro.misc;

import java.util.function.Function;

/**
 * Created by ezhulkov on 19.07.15.
 */
@FunctionalInterface
public interface EndpointCallback<T, R> extends Function<T, R> {

    @Override
    default R apply(T body) {
        try {
            return applyThrowing(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    R applyThrowing(T body) throws Exception;

}
