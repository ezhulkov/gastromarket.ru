package org.ohm.gastro.domain;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

/**
 * Created by ezhulkov on 24.08.14.
 */
@MappedSuperclass
public abstract class AbstractBaseEntity implements BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sequence", allocationSize = 1, initialValue = 30000)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
