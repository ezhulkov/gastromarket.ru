package org.ohm.gastro.gui.misc;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;

import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 04.10.14.
 */
public class OptionGroupModelImpl<T> implements OptionGroupModel {

    private final String name;
    private final List<OptionModel> options;

    public OptionGroupModelImpl(String name, List<OptionModel> options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public Map<String, String> getAttributes() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        return options;
    }

}
