package org.ohm.gastro.gui.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.ohm.gastro.domain.CategoryEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CategorySelectModel extends GenericSelectModel<CategoryEntity> {

    private final List<OptionModel> options;
    private final List<OptionGroupModel> groups;
    private final BiMap<String, CategoryEntity> objects;


    public CategorySelectModel(Collection<CategoryEntity> list, PropertyAccess access) {
        super(list, CategoryEntity.class, "name", "id", access);
        this.options = ImmutableList.copyOf(list.stream()
                                                    .filter(t -> t.getChildren().size() == 0)
                                                    .map(t -> new OptionModelImpl(t.getName(), t))
                                                    .collect(Collectors.toList()));
        this.groups = ImmutableList.copyOf(list.stream()
                                                   .filter(t -> t.getChildren().size() > 0)
                                                   .map(t -> new OptionGroupModelImpl<>(t.getName(), getOptionModel(t.getChildren())))
                                                   .collect(Collectors.toList()));
        this.objects = ImmutableBiMap.copyOf(list.stream().flatMap(t -> t.getChildren().size() == 0 ? Stream.of(t) : t.getChildren().stream())
                                                     .collect(Collectors.toMap(t -> t.getId().toString(),
                                                                               t -> t)));
    }

    @Override
    protected List<OptionModel> getOptionModel(final Collection<CategoryEntity> list) {
        return ImmutableList.copyOf(list.stream()
                                            .map(t -> new OptionModelImpl((t.getParent() != null ? t.getParent().getName() + " - " : "") + t.getName(), t))
                                            .collect(Collectors.toList()));
    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return groups;
    }

    @Override
    public List<OptionModel> getOptions() {
        return options;
    }

    @Override
    public CategoryEntity toValue(String key) {
        CategoryEntity categoryEntity = objects.get(key);
        if (categoryEntity == null) {
            categoryEntity = (CategoryEntity) groups.stream()
                    .flatMap(t -> t.getOptions().stream())
                    .filter(optionModel -> optionModel.getLabel().equals(key))
                    .map(OptionModel::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return categoryEntity;
    }

    @Override
    public String toClient(CategoryEntity obj) {
        return objects.inverse().get(obj);
    }

}