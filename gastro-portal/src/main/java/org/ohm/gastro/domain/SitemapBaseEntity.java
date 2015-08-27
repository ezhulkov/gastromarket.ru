package org.ohm.gastro.domain;

import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Created by ezhulkov on 24.08.14.
 */
@MappedSuperclass
public abstract class SitemapBaseEntity extends AltIdBaseEntity implements SitemapEntity {

    @Column(name = "last_modified")
    private Date lastModified;

    public Date getLastModified() {
        return lastModified;
    }

    public String getLastModifiedPrintable() {
        return CommonsUtils.SITEMAP_DATE.get().format(lastModified == null ? new Date() : lastModified);
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
