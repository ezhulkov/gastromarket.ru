package org.ohm.gastro.dto;

import org.ohm.gastro.domain.UserEntity;

/**
 * Created by ezhulkov on 03.03.16.
 */
public class UserDTO {

    private final UserEntity user;

    public UserDTO(UserEntity user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getLinkName();
    }

    public String getAvatar() {
        return user.getLinkAvatar();
    }

    public String getUrl() {
        return user.getLinkUrl();
    }

}
