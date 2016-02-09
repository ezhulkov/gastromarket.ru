package org.ohm.gastro.gui.dto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ezhulkov on 09.02.16.
 */
public class BreadcrumbsList {

    private List<Breadcrumb> breadcrumbs = Lists.newArrayList();

    public void addAll(List<Breadcrumb> breadcrumbs) {
        this.breadcrumbs.clear();
        this.breadcrumbs.addAll(breadcrumbs);
    }

    public List<Breadcrumb> getBreadcrumbs() {
        return breadcrumbs;
    }

}
