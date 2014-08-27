package org.ohm.gastro.gui.services.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.util.AbstractSelectModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericSelectModel<T> extends AbstractSelectModel implements ValueEncoder<T> {

    private final PropertyAdapter labelFieldAdapter;
    private final PropertyAdapter idFieldAdapter;
    private final List<OptionModel> options;
    private final BiMap<String, T> objects;

    public GenericSelectModel(Collection<T> list,
                              Class<T> clazz, String labelField,
                              String idField, PropertyAccess access) {
        this.idFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(idField);
        this.labelFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(labelField);
        this.options = ImmutableList.copyOf(list.stream()
                                                    .map(t -> new OptionModelImpl(labelFieldAdapter.get(t).toString(), t))
                                                    .collect(Collectors.toList()));
        Map<String, T> objects = Maps.newHashMap();
        list.stream().forEach(t -> {
            String key = idFieldAdapter.get(t).toString();
            objects.put(key, t);
        });
        this.objects = ImmutableBiMap.copyOf(objects);

    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        return options;
    }

    @Override
    public String toClient(T obj) {
        return objects.inverse().get(obj);
    }

    @Override
    public T toValue(String key) {
        return objects.get(key);
    }

}