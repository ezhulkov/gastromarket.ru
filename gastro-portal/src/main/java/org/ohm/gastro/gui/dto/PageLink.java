package org.ohm.gastro.gui.dto;

/**
 * Created by ezhulkov on 01.11.15.
 */
public class PageLink {

    private final String name;
    private final String url;

    public PageLink(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PageLink pageLink = (PageLink) o;

        if (url != null ? !url.equals(pageLink.url) : pageLink.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
