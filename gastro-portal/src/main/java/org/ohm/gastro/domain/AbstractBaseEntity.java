package org.ohm.gastro.domain;

import org.hibernate.proxy.HibernateProxy;

/**
 * Created by ezhulkov on 24.08.14.
 */
public abstract class AbstractBaseEntity implements BaseEntity {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getRealClass(this) != getRealClass(o)) return false;
        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return getId() == null ? that.getId() == null : getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id='" + getId() + '\'' +
                '}';
    }

    public static Class getRealClass(Object proxy) {
        if (proxy instanceof HibernateProxy) {
            return ((HibernateProxy) proxy).getHibernateLazyInitializer()
                    .getImplementation()
                    .getClass();
        } else {
            return proxy.getClass();
        }
    }

}
