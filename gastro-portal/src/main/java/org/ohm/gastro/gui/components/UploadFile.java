package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class UploadFile extends BaseComponent {

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String objectId;

    @Property
    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private FileType type;

    @Property
    @Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private ImageSize responseSize;

    @Property
    @Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private String imageUrl;

    @Property
    @Parameter(name = "class", defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Property
    @Parameter(name = "inputType", defaultPrefix = BindingConstants.LITERAL, value = "button")
    private String inputType;

    public boolean isButton() {
        return "button".equals(inputType);
    }

}
