package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 24.08.14.
 */
public abstract class AbstractBaseEntity implements BaseEntity {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractBaseEntity that = (AbstractBaseEntity) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
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

}
