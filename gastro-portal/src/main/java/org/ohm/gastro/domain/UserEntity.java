package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
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
import java.util.Collection;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "person")
public class UserEntity extends AbstractBaseEntity implements UserDetails {

    public enum Type {
        ADMIN, COOK, USER
    }

    public enum Status {
        ENABLED, DISABLED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "user")
    private Long id;

    @Column
    private boolean anonymous = false;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.ENABLED;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column
    private Integer bonus = 0;

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/avatar-stub.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/avatar-stub-medium.png";


    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/avatar-stub-small.png";

    @Column
    @Enumerated(EnumType.STRING)
    private Type type = Type.USER;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<CatalogEntity> cooks = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private UserEntity referrer;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserEntity getReferrer() {
        return referrer;
    }

    public void setReferrer(UserEntity referrer) {
        this.referrer = referrer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<CatalogEntity> getCooks() {
        return cooks;
    }

    public void setCooks(List<CatalogEntity> cooks) {
        this.cooks = cooks;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Lists.newArrayList(new SimpleGrantedAuthority(type.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Status.ENABLED;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getPrintableName() {
        return StringUtils.isEmpty(fullName) ? email : fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public void setAvatarUrlSmall(String avatarUrlSmall) {
        this.avatarUrlSmall = avatarUrlSmall;
    }

    public String getAvatarUrlMedium() {
        return avatarUrlMedium;
    }

    public void setAvatarUrlMedium(String avatarUrlMedium) {
        this.avatarUrlMedium = avatarUrlMedium;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }

}
