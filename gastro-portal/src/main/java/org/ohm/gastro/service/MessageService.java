package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface MessageService {

    int getUnreadMessages(UserEntity user);

}
