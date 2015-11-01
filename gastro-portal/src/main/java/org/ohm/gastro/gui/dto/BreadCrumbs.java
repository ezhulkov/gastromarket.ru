package org.ohm.gastro.gui.dto;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 01.11.15.
 */
public final class BreadCrumbs {

    private final List<PageLink> breadCrumbs = Lists.newArrayList();

    public void addItem(String title, String page) {
        final PageLink newLink = new PageLink(title, page);
        if (!breadCrumbs.stream().reduce((a, b) -> b).filter(t -> t.equals(newLink)).isPresent()) {
            breadCrumbs.add(newLink);
        }
    }

    public List<PageLink> getLastBreadCrumbs(String currentPage) {
        return breadCrumbs.stream().skip(Math.max(0, breadCrumbs.lastIndexOf(new PageLink(null, currentPage)) - 2)).limit(3).collect(Collectors.toList());
    }

}
