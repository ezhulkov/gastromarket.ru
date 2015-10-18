package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class E404 extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllActiveCatalogs().stream().limit(5).collect(Collectors.toList());
    }

    public String getRootProperties() {
        return getProductService().findAllRootValues(oneCatalog, true).stream()
                .map(PropertyValueEntity::getName)
                .distinct()
                .collect(Collectors.joining(", "));
    }

}
