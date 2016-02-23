package org.ohm.gastro.gui.dto;

/**
 * Created by ezhulkov on 09.02.16.
 */
public class Breadcrumb {

    private final Class page;
    private final Object[] params;
    private final String title;

    public Breadcrumb(String title, Class page, Object[] params) {
        this.page = page;
        this.params = params;
        this.title = title;
    }

    public static Breadcrumb of(String title, Class page) {
        return new Breadcrumb(title, page, null);
    }

    public static Breadcrumb of(String title, Class page, Object... params) {
        return new Breadcrumb(title, page, params);
    }

    public Class getPage() {
        return page;
    }

    public Object[] getParams() {
        return params;
    }

    public String getTitle() {
        return title;
    }

}
