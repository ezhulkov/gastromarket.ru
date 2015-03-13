package org.ohm.gastro.gui.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.ohm.gastro.domain.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GenericSelectModel<T extends BaseEntity> extends AbstractSelectModel implements ValueEncoder<T> {

    private final List<OptionModel> options;
    private final BiMap<String, T> objects;

    protected final PropertyAdapter idFieldAdapter;
    protected final PropertyAdapter labelFieldAdapter;

    public GenericSelectModel(Collection<T> list,
                              Class<T> clazz, String labelField,
                              String idField, PropertyAccess access) {
        idFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(idField);
        labelFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(labelField);
        this.options = ImmutableList.copyOf(list.stream()
                                                    .map(t -> new OptionModelImpl(labelFieldAdapter.get(t).toString(), t))
                                                    .collect(Collectors.toList()));
        this.objects = ImmutableBiMap.copyOf(list.stream()
                                                     .collect(Collectors.toMap(t -> idFieldAdapter.get(t).toString(),
                                                                               t -> t)));

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