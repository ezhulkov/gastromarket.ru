package org.ohm.gastro.gui.pages.chef;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllCatalogs().stream().filter(CatalogEntity::getWasSetup).collect(Collectors.toList());
    }

}
