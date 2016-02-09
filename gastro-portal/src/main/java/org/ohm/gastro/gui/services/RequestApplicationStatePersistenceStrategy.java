package org.ohm.gastro.gui.services;

import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStatePersistenceStrategy;
import org.apache.tapestry5.services.Request;

/**
 * Created by ezhulkov on 09.02.16.
 */
public class RequestApplicationStatePersistenceStrategy implements ApplicationStatePersistenceStrategy {

    private final Request request;

    public RequestApplicationStatePersistenceStrategy(Request request) {
        this.request = request;
    }

    @Override
    public <T> T get(Class<T> clazz, ApplicationStateCreator<T> creator) {
        return getOrCreate(clazz, creator);
    }

    @Override
    public <T> void set(Class<T> clazz, T obj) {
        final String key = buildKey(clazz);
        request.setAttribute(key, obj);
    }

    @Override
    public <T> boolean exists(Class<T> clazz) {
        final String key = buildKey(clazz);
        return request.getAttribute(key) != null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getOrCreate(Class<T> clazz, ApplicationStateCreator<T> creator) {
        final String key = buildKey(clazz);
        final Object object = request.getAttribute(key);
        if (object != null && !object.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException();
        }
        T result = (T) object;
        if (result == null) {
            result = creator.create();
            set(clazz, result);
        }
        return result;
    }

    private String buildKey(Class clazz) {
        return clazz.getName();
    }

}
