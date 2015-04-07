package org.ohm.gastro.gui.pages.office;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.service.social.MediaElement;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Import extends BaseComponent {

    @Property
    private String socialCode;

    @Property
    private MediaElement oneElement;

    @Cached
    public Collection<String> getSocialCodes() {
        return getTokens().keySet().stream()
                .filter(t -> getApplicationContext().getBean(t, SocialSource.class) instanceof MediaImportService)
                .collect(Collectors.toList());
    }

    public String getSocialName() {
        return getApplicationContext().getBean(socialCode, MediaImportService.class).getSocialSourceName();
    }

    public List<MediaElement> getElements() {
        return getToken(socialCode)
                .map(token -> getApplicationContext().getBean(socialCode, MediaImportService.class).getElements(token))
                .orElse(Lists.newArrayList());
    }

}
