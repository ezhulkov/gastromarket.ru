package org.ohm.gastro.service;

import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.scribe.model.Token;

import java.util.List;

/**
 * Created by ezhulkov on 05.04.15.
 */
public interface MediaImportService extends SocialSource {

    List<MediaAlbum> getAlbums(Token token);

    List<MediaElement> getElements(Token token);

}
