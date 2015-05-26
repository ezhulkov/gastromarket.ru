package org.ohm.gastro.domain;

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
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "message")
public class MessageEntity extends AbstractBaseEntity {

    public enum Status {
        NEW, SENT, READ, DELETED
    }

    public enum RecipientType {
        ALL, COOKS, USER
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, targetEntity = UserEntity.class)
    private UserEntity sender;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, targetEntity = UserEntity.class)
    private UserEntity recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_status")
    private Status senderStatus = Status.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_status")
    private Status recipientStatus = Status.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type")
    private RecipientType recipientType = RecipientType.USER;

    @Column
    private String subject;

    @Column
    private String body;

    @Column(name = "create_date")
    private Date createDate = new Date(System.currentTimeMillis());

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getRecipient() {
        return recipient;
    }

    public void setRecipient(UserEntity recipient) {
        this.recipient = recipient;
    }

    public Status getSenderStatus() {
        return senderStatus;
    }

    public void setSenderStatus(Status senderStatus) {
        this.senderStatus = senderStatus;
    }

    public Status getRecipientStatus() {
        return recipientStatus;
    }

    public void setRecipientStatus(Status recipientStatus) {
        this.recipientStatus = recipientStatus;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}