package org.ohm.gastro.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "conversation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConversationEntity extends AbstractBaseEntity implements CommentableEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, targetEntity = UserEntity.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity sender;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, targetEntity = UserEntity.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity recipient;

    @Column(name = "last_seen")
    private Date lastSeenDate = new Date();

    @Column
    private Date date = new Date();

    @Column(name = "last_action_date")
    private Date lastActionDate = new Date();

    public Date getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(final Date lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(final UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getRecipient() {
        return recipient;
    }

    public void setRecipient(final UserEntity recipient) {
        this.recipient = recipient;
    }

    public Date getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(final Date lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    @Override
    public Type getCommentableType() {
        return Type.CONVERSATION;
    }

    public Optional<UserEntity> getOpponent(UserEntity user) {
        return Optional.ofNullable(sender.equals(user) ? recipient : recipient.equals(user) ? sender : null);
    }

    public String getOpponentLink(final UserEntity user) {
        return getOpponentInfo(user, t -> t.getFirstCatalog().map(CatalogEntity::getFullUrl).orElse(t.getFullUrl()));
    }

    public String getOpponentName(final UserEntity user) {
        return getOpponentInfo(user, t -> t.getFirstCatalog().map(CatalogEntity::getName).orElse(t.getFullName()));
    }

    public String getOpponentAvatar(final UserEntity user) {
        return getOpponentInfo(user, t -> t.getFirstCatalog().map(CatalogEntity::getAvatarUrlMedium).orElse(t.getAvatarUrlMedium()));
    }

    public String getFullUrl() {
        return "http://gastromarket.ru/office/message/" + getId();
    }

    private <T> T getOpponentInfo(final UserEntity user, final Function<UserEntity, T> f) {
        return getOpponent(user).map(f).orElse(null);
    }


}