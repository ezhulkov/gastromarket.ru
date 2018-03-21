package org.ohm.gastro.service.social;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaElement {

    private final String id;
    private final String link;
    private final String avatarUrl;
    private final String avatarUrlSmall;
    private final String caption;

    private boolean checked = false;

    public MediaElement(final String id, final String link, final String caption, final String avatarUrl, final String avatarUrlSmall) {
        this.id = id;
        this.link = link;
        this.avatarUrl = avatarUrl;
        this.avatarUrlSmall = avatarUrlSmall;
        this.caption = caption;
    }

    public String getId() {
        return id;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public String getLink() {
        return link;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCaption() {
        return StringUtils.isEmpty(caption) ? "Фотография" : caption;
    }

    public boolean isChecked() {
        return checked;
    }

    public void toggle() {
        this.checked = !checked;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MediaElement that = (MediaElement) o;

        if (link != null ? !link.equals(that.link) : that.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return link != null ? link.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MediaElement{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarUrlSmall='" + avatarUrlSmall + '\'' +
                '}';
    }

}
