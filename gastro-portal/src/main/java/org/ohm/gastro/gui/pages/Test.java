package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Test extends BaseComponent {

    @Property
    private Region region;

    public void onActivate() {
        final Region boundRegion = RegionFilter.REGION_THREAD_LOCAL.get();
        this.region = boundRegion == null ? Region.DEFAULT : boundRegion;
    }

}
