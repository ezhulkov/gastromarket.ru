package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CommentableEntity extends BaseEntity {

    public enum Type {
        CATALOG, USER, ORDER
    }

    public Type getCommentableType();

}