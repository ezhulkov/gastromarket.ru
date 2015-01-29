package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Random;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    private final Random random = new Random();

    @Property
    private CategoryEntity category;

    @Property
    private ProductEntity oneProduct;

    @Inject
    @Property
    private Block productsBlock;

    public int getHeight() {
        return 270 - random.nextInt(50);
    }

    public java.util.List<ProductEntity> getProducts() {
        return getCatalogService().findAllProducts(null, null, false);
    }

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextTexts(int lastIndex) {
        return productsBlock;
    }

    public boolean onActivate() {
        category = null;
        return true;
    }

    public boolean onActivate(Long cid) {
        category = getCatalogService().findCategory(cid);
        return true;
    }

    public Object[] onPassivate() {
        return category == null ? null : new Object[]{category.getId()};
    }


}
