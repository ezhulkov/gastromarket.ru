package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 31.08.14.
 */
public abstract class AbstractOfferPage extends AbstractPage {

    @Property
    protected OfferEntity offer;

    @Property
    private ProductEntity product;

}
