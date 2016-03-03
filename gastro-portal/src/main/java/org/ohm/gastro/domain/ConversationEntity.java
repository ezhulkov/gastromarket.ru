package org.ohm.gastro.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Optional;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "conversation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConversationEntity extends AbstractBaseEntity implements CommentableEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, targetEntity = UserEntity.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity author;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, targetEntity = UserEntity.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity opponent;

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

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(final UserEntity author) {
        this.author = author;
    }

    public UserEntity getOpponent() {
        return opponent;
    }

    public void setOpponent(final UserEntity opponent) {
        this.opponent = opponent;
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
        return Optional.ofNullable(author.equals(user) ? opponent : opponent.equals(user) ? author : null);
    }

    public String getOpponentLink(final UserEntity user) {
        return getOpponent(user).map(UserEntity::getLinkUrl).orElse(null);
    }

    public String getOpponentName(final UserEntity user) {
        return getOpponent(user).map(UserEntity::getLinkName).orElse(null);
    }

    public Long getOpponentId(final UserEntity user) {
        return getOpponent(user).map(UserEntity::getId).orElse(null);
    }

    public String getOpponentAvatar(final UserEntity user) {
        return getOpponent(user).map(UserEntity::getLinkAvatar).orElse(null);
    }

    public String getFullUrl() {
        return "https://gastromarket.ru/office/message/" + getId();
    }

    public String getLastActionDatePrintable() {
        return lastActionDate == null ? "" : CommonsUtils.GUI_DATE_LONG.get().format(lastActionDate);
    }

}