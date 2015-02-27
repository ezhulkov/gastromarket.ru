package org.ohm.gastro.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Supplier;

/**
 * Created by ezhulkov on 05.02.15.
 */
public class CommonsUtils {

    public static final ThreadLocal<DateFormat> ORDERDATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("ddMMyyyy");
        }
    };

    public static final ThreadLocal<DateFormat> GUIDATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };

    public static final ThreadLocal<DateFormat> GUIDATE_LONG = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        }
    };

    public static <T> T coalesceLazy(final T value, final Supplier<? extends T>... suppliers) {
        if (value != null) {
            return value;
        }
        for (Supplier<? extends T> supplier : suppliers) {
            final T v = supplier.get();
            if (v != null) {
                return v;
            }
        }
        return null;
    }

}
