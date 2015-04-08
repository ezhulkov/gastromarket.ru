package org.ohm.gastro.service;

import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaResponse;
import org.scribe.model.Token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by ezhulkov on 05.04.15.
 */
public interface MediaImportService extends SocialSource {

    List<MediaAlbum> getAlbums(@Nonnull Token token);

    MediaResponse getElements(@Nonnull Token token, @Nullable Object context);

}
