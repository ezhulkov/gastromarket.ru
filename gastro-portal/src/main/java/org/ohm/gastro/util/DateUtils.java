package org.ohm.gastro.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ezhulkov on 05.02.15.
 */
public class DateUtils {

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

}
