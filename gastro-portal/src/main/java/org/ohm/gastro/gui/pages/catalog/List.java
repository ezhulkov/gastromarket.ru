package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CategoryEntity category;

    @Property
    private int lastIndex = 0;

    @Property
    private String oneText;

    @Inject
    @Property
    private Block textBlock;

    public java.util.List<String> getTexts() {
        lastIndex += 5;
        return IntStream.range(lastIndex - 5, lastIndex).mapToObj(t -> "" + t).distinct().collect(Collectors.toList());
    }

    @OnEvent(value = EventConstants.ACTION, component = "nextTexts")
    public Block fetchNextTexts(int lastIndex) {
        this.lastIndex = lastIndex;
        return textBlock;
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
