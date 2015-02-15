package org.ohm.gastro.domain;

import com.google.common.collect.Lists;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "purchase")
public class PurchaseEntity extends AbstractBaseEntity {

    public enum Status {
        NEW, ACCEPTED, READY, DELIVERED, CANCELLED, CLOSED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "order")
    private Long id;

    @Column
    private String comment;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase")
    private List<PurchaseProductEntity> products = Lists.newArrayList();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public void setCustomer(UserEntity customer) {
        this.customer = customer;
    }

    public List<PurchaseProductEntity> getProducts() {
        return products;
    }

    public void setProducts(final List<PurchaseProductEntity> products) {
        this.products = products;
    }

}
