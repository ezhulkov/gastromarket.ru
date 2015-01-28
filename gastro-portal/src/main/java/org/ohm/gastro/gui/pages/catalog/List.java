package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    private final Random random = new Random();

    @Property
    private CategoryEntity category;

    @Property
    private int lastIndex = 0;

    @Property
    private String oneProduct;

    @Inject
    @Property
    private Block productsBlock;

    public java.util.List<String> getProducts() {
        lastIndex += 20;
        return IntStream.range(lastIndex - 20, lastIndex).mapToObj(t -> "" + t).distinct().collect(Collectors.toList());
    }

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextTexts(int lastIndex) {
        this.lastIndex = lastIndex;
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
