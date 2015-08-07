package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShow extends BaseComponent {

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private OrderEntity.Status status;

    @Property
    private CatalogEntity catalog;

    public List<CatalogEntity> getCatalogs() {
        if (status == Status.NEW) return getShoppingCart().getCatalogs().stream().map(Entry::getKey).collect(Collectors.toList());
        return Lists.newArrayList();
    }

}