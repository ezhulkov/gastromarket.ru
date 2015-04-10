package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Import extends BaseComponent {

    @Property
    private String socialCode;

    @Property
    private MediaElement oneElement;

    @Property
    @Inject
    private Block elementsBlock;

    @Property
    @Inject
    private Block elementBlock;

    @Property
    private CatalogEntity catalog;

    @Property
    private String albumId;

    @Property
    private MediaAlbum album;

    @Persist
    private Map<String, Set<MediaElement>> cachedElements;

    private String context;

    public void onActivate(Long cid) {
        catalog = getCatalogService().findCatalog(cid);
        if (cachedElements == null) cachedElements = new HashMap<>();
    }

    public Long onPassivate() {
        return catalog == null ? null : catalog.getId();
    }

    @Cached
    public Collection<String> getSocialCodes() {
        return getTokens().keySet().stream()
                .filter(t -> getApplicationContext().getBean(t, SocialSource.class) instanceof MediaImportService)
                .collect(Collectors.toList());
    }

    public String getSocialName() {
        return getApplicationContext().getBean(socialCode, MediaImportService.class).getSocialSourceName();
    }

    @Cached
    public List<MediaAlbum> getAlbums() {
        return getToken(socialCode).map(token -> getApplicationContext().getBean(socialCode, MediaImportService.class).getAlbums(token))
                .orElse(null);
    }

    @Cached
    public MediaResponse getElements() {
        final MediaResponse mediaResponse = getToken(socialCode)
                .map(token -> getApplicationContext().getBean(socialCode, MediaImportService.class).getImages(token, albumId, context))
                .orElse(null);
        synchronized (Import.class) {
            Set<MediaElement> mediaElements = cachedElements.get(socialCode);
            if (mediaElements == null) {
                mediaElements = new HashSet<>();
                cachedElements.put(socialCode, mediaElements);
            }
            mediaElements.addAll(mediaResponse.getMediaElements());
        }
        return mediaResponse;
    }

    public String getElementsZone() {
        return "elementsZone" + socialCode;
    }

    public String getElementZone() {
        return "elementZone" + oneElement.getLink();
    }

    public Block onActionFromInitialFetchElements(String socialCode) {
        this.socialCode = socialCode;
        this.context = null;
        return elementsBlock;
    }

    public Block onActionFromFetchElements(String socialCode, String context) {
        this.socialCode = socialCode;
        this.context = context;
        return elementsBlock;
    }

    public Block onActionFromCheckElement(String socialCode, String link) {
        this.socialCode = socialCode;
        this.oneElement = cachedElements.get(socialCode).stream().filter(t -> t.getLink().equals(link)).findFirst().get();
        this.oneElement.toggle();
        return elementBlock;
    }

    public boolean isChecked() {
        return cachedElements.get(socialCode).stream().filter(t -> t.equals(oneElement)).findFirst().get().isChecked();
    }

    public Link onActionFromImport() {
        getProductService().importProducts(cachedElements, catalog);
        return getPageLinkSource().createPageRenderLinkWithContext(ImportResults.class, catalog.getId());
    }

    public Block onActionFromSelectAlbum(String albumId, String socialCode) {
        this.albumId = albumId;
        this.socialCode = socialCode;
        return elementsBlock;
    }

}
