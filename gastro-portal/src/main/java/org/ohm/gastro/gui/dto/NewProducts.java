package org.ohm.gastro.gui.dto;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.ProductEntity;

import java.util.List;

/**
 * Created by ezhulkov on 01.10.15.
 */
public class NewProducts {

    private final List<ProductEntity> items = Lists.newLinkedList();

    public List<ProductEntity> getItems() {
        return items;
    }

}
