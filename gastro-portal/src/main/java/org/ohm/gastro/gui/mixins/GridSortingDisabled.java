package org.ohm.gastro.gui.mixins;

import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.MixinAfter;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.corelib.components.Grid;

import java.util.List;

@MixinAfter
public class GridSortingDisabled {

    @InjectContainer
    private Grid grid;

    private void setupRender() {
        if (grid != null && grid.getDataSource() != null && grid.getDataSource().getAvailableRows() != 0) {
            BeanModel model = grid.getDataModel();
            List propertyNames = model.getPropertyNames();
            for (Object propNameObj : propertyNames) {
                String propName = (String) propNameObj;
                PropertyModel propModel = model.get(propName);
                propModel.sortable(false);
            }
        }
    }

}