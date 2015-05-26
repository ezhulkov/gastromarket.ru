package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ohm.gastro.util.CommonsUtils;
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
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity extends AbstractBaseEntity implements UserDetails {

    public enum Type {
        ADMIN, COOK, USER
    }

    public enum Status {
        ENABLED, DISABLED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    private Date date = new Date();

    @Column(name = "login_date")
    private Date loginDate;

    @Column
    private Integer bonus = 0;

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/avatar-stub-210x210.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/avatar-stub-100x100.png";


    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/avatar-stub-23x23.png";

    @Column
    @Enumerated(EnumType.STRING)
    private Type type = Type.USER;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<CatalogEntity> catalogs = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public List<CatalogEntity> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<CatalogEntity> catalogs) {
        this.catalogs = catalogs;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(final Date loginDate) {
        this.loginDate = loginDate;
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

    public boolean isUser() {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("USER"));
    }

    public boolean isCook() {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("COOK"));
    }

    public boolean isAdmin() {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    public String getLoginDatePrintable() {
        return loginDate == null ? "-" : CommonsUtils.GUI_DATE_LONG.get().format(loginDate);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

}
