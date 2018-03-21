package org.ohm.gastro.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "notification_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NotificationConfigEntity extends AbstractBaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "mail_type")
    private MailService.MailType mailType;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity user;

    @Column(name = "enabled")
    private boolean enabled = false;

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "NotificationConfigEntity{" +
                "id=" + getId() +
                ", mailType=" + mailType +
                ", user=" + user +
                ", enabled=" + enabled +
                '}';
    }

}
