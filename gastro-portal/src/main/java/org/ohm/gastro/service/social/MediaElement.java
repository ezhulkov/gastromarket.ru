package org.ohm.gastro.service.social;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaElement {

    private final String name;
    private final String avatarUrl;
    private final String text;

    public MediaElement(final String name, final String avatarUrl, final String text) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getText() {
        return text;
    }

}
