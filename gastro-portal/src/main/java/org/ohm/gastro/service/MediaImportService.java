package org.ohm.gastro.service;

import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;

import java.util.List;

/**
 * Created by ezhulkov on 05.04.15.
 */
public interface MediaImportService {

    List<MediaAlbum> getAlbums();

    List<MediaElement> getElements();

}
