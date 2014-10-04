package org.ohm.gastro.gui.misc;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.ohm.gastro.domain.CategoryEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySelectModel extends GenericSelectModel<CategoryEntity> {

    private final List<OptionModel> options;
    private final List<OptionGroupModel> groups;

    public CategorySelectModel(Collection<CategoryEntity> list, PropertyAccess access) {
        super(list, CategoryEntity.class, "name", "id", access);
        this.options = ImmutableList.copyOf(list.stream()
                                                    .filter(t -> CollectionUtils.isEmpty(t.getChildren()))
                                                    .map(t -> new OptionModelImpl(labelFieldAdapter.get(t).toString(), t))
                                                    .collect(Collectors.toList()));
        this.groups = ImmutableList.copyOf(list.stream()
                                                   .filter(t -> CollectionUtils.isNotEmpty(t.getChildren()))
                                                   .map(t -> new OptionGroupModelImpl<>(t.getName(), getOptionModel(t.getChildren())))
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
        CategoryEntity categoryEntity = super.toValue(key);
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

}