package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface SitemapEntity extends BaseEntity {

    String getLastModifiedPrintable();

    String getLocationUrl();

}