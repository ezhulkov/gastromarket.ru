package org.ohm.gastro.util;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.ohm.gastro.trait.Logging;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 05.02.15.
 */
public class CommonsUtils {

    private static final Map<Character, String> REPLACEMENTS = new HashMap<Character, String>() {
        {
            put(' ', "-");
            put('–', "-");
            put('=', "-");
            put('&', "n");
            put('№', "N");
            put('а', "a");
            put('б', "b");
            put('в', "v");
            put('г', "g");
            put('д', "d");
            put('е', "e");
            put('ё', "e");
            put('ж', "zh");
            put('з', "z");
            put('и', "i");
            put('й', "i");
            put('к', "k");
            put('л', "l");
            put('м', "m");
            put('н', "n");
            put('о', "o");
            put('п', "p");
            put('р', "r");
            put('с', "s");
            put('т', "t");
            put('у', "u");
            put('ф', "f");
            put('х', "h");
            put('ц', "ts");
            put('ч', "ch");
            put('ш', "sh");
            put('щ', "sch");
            put('ь', "");
            put('ы', "y");
            put('ъ', "");
            put('э', "e");
            put('ю', "yu");
            put('я', "ya");
            put('a', "a");
            put('b', "b");
            put('c', "c");
            put('d', "d");
            put('e', "e");
            put('f', "f");
            put('g', "g");
            put('h', "h");
            put('i', "i");
            put('j', "j");
            put('k', "k");
            put('l', "l");
            put('m', "m");
            put('n', "n");
            put('o', "o");
            put('p', "p");
            put('q', "q");
            put('r', "r");
            put('s', "s");
            put('t', "t");
            put('u', "u");
            put('v', "v");
            put('w', "w");
            put('x', "x");
            put('y', "y");
            put('z', "z");
            put('0', "0");
            put('1', "1");
            put('2', "2");
            put('3', "3");
            put('4', "4");
            put('5', "5");
            put('6', "6");
            put('7', "7");
            put('8', "8");
            put('9', "9");
        }
    };

    public static final ThreadLocal<DateFormat> ORDER_DATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("ddMMyy");
        }
    };

    public static final ThreadLocal<DateFormat> GUI_DATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };

    public static final ThreadLocal<DateFormat> SITEMAP_DATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public static final ThreadLocal<DateFormat> GUI_DATE_LONG = new ThreadLocal<DateFormat>() {
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

    public static String transliterate(String text) {
        if (text != null) text = text.trim();
        if (text != null && text.length() != 0) {
            text = text.toLowerCase();
            text = text.replaceAll("(\r\n|\r|\n|\n\r|\t| )", " ");
            StringBuilder strResult = new StringBuilder();
            char[] textChars = text.toCharArray();
            for (char textChar : textChars) {
                String translateChar = REPLACEMENTS.get(textChar);
                if (translateChar != null) strResult.append(translateChar);
            }
            return strResult.toString();
        } else {
            return text;
        }
    }

    public static void dumpHttpServletRequest(final HttpServletRequest request) {
        Logging.logger.info("Method: {}", request.getMethod());
        Logging.logger.info("Url: {}", request.getRequestURI());
        Collections.list(request.getHeaderNames()).forEach(t -> Logging.logger.info("Header: {} Value: {}", t, request.getHeader(t)));
        request.getParameterMap().entrySet().stream().forEach(t -> Logging.logger.info("Parameter: {} Value: {}", t.getKey(), Arrays.stream(t.getValue()).collect(Collectors.joining(","))));
    }

    public static boolean checkAjaxBotRequest(final HttpServletRequest request) {
        if (request.getParameter("t:zoneid") == null || request.getParameter("t:submit") == null) {
            Logging.logger.error("Bot detected");
            CommonsUtils.dumpHttpServletRequest(request);
            return true;
        }
        return false;
    }

    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

}
