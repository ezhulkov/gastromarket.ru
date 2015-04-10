package org.ohm.gastro.service.social;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaElement {

    private final String link;
    private final String avatarUrl;
    private final String caption;

    private boolean checked = false;

    public MediaElement(final String link, final String avatarUrl, final String caption) {
        this.link = link;
        this.avatarUrl = avatarUrl;
        this.caption = caption;
    }

    public String getLink() {
        return link;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCaption() {
        return caption;
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
                "link='" + link + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

}
