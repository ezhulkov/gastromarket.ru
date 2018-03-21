package org.ohm.gastro.gui.misc;

import com.google.common.collect.ImmutableMap;
import org.apache.tapestry5.OptionModel;

import java.util.Map;

/**
 * Created by ezhulkov on 13.03.15.
 */
public class OptionModelImpl implements OptionModel {

    private final String label;
    private final Object value;
    private final Map<String, String> attributes;

    public OptionModelImpl(String label, Object value, String cssClass) {
        this.label = label;
        this.value = value;
        this.attributes = ImmutableMap.of("class", cssClass);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
