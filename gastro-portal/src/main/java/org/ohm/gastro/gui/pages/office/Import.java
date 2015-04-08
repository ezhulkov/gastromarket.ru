package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;

import java.util.Collection;
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

    private String context;

    @Cached
    public Collection<String> getSocialCodes() {
        return getTokens().keySet().stream()
                .filter(t -> getApplicationContext().getBean(t, SocialSource.class) instanceof MediaImportService)
                .collect(Collectors.toList());
    }

    public String getSocialName() {
        return getApplicationContext().getBean(socialCode, MediaImportService.class).getSocialSourceName();
    }

    public MediaResponse getElements() {
        return getToken(socialCode)
                .map(token -> getApplicationContext().getBean(socialCode, MediaImportService.class).getElements(token, context))
                .orElse(null);
    }

    public String getElementsZone() {
        return "elementsZone" + socialCode;
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

}
