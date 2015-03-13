package org.ohm.gastro.gui.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.ohm.gastro.domain.CategoryEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CategorySelectModel extends GenericSelectModel<CategoryEntity> {

    private final List<OptionModel> options;
    private final BiMap<String, CategoryEntity> objects;

    public CategorySelectModel(Collection<CategoryEntity> list, PropertyAccess access) {
        super(list, CategoryEntity.class, "name", "id", access);
        this.options = ImmutableList.copyOf(list.stream()
                                                    .flatMap(t -> Stream.concat(Stream.of(t), t.getChildren().stream()))
                                                    .map(t -> new OptionModelImpl((t.getParent() == null ? "" : t.getParent().getName() + " - ") + t.getName(),
                                                                                  t,
                                                                                  t.getChildren().isEmpty() ? "leaf" : "root"))
                                                    .collect(Collectors.toList()));
        this.objects = ImmutableBiMap.copyOf(list.stream()
                                                     .flatMap(t -> Stream.concat(Stream.of(t), t.getChildren().stream()))
                                                     .collect(Collectors.toMap(t -> t.getId().toString(),
                                                                               t -> t)));
    }

    @Override
    public List<OptionModel> getOptions() {
        return options;
    }

    @Override
    public CategoryEntity toValue(String key) {
        return objects.get(key);
    }

    @Override
    public String toClient(CategoryEntity obj) {
        return objects.inverse().get(obj);
    }

}