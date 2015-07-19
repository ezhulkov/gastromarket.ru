package org.ohm.gastro.service.social;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaResponse {

    private final String context;
    private final List<MediaElement> mediaElements = Lists.newArrayList();

    public MediaResponse() {
        this.context = null;
    }

    public MediaResponse(final String context, final List<MediaElement> mediaElements) {
        this.context = context;
        if (mediaElements != null) this.mediaElements.addAll(mediaElements);
    }

    public String getContext() {
        return context;
    }

    public List<MediaElement> getMediaElements() {
        return mediaElements;
    }

}
