package org.ohm.gastro.service.social;

import java.util.List;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaResponse {

    private final String context;
    private final List<MediaElement> mediaElements;

    public MediaResponse(final String context, final List<MediaElement> mediaElements) {
        this.context = context;
        this.mediaElements = mediaElements;
    }

    public String getContext() {
        return context;
    }

    public List<MediaElement> getMediaElements() {
        return mediaElements;
    }

}
