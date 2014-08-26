package org.ohm.gastro.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "property_value")
public class PropertyValueEntity implements BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "value")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "value")
    private Long id;

    @Column
    private String value;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private PropertyEntity property;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PropertyEntity getProperty() {
        return property;
    }

    public void setProperty(PropertyEntity property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "PropertyValueEntity{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", property=" + property +
                '}';
    }

}
