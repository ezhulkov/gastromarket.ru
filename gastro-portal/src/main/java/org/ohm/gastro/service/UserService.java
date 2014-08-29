package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService {

    public List<UserEntity> findAllUser();

    void deleteUser(Long id);

    void saveUser(UserEntity user);

    UserEntity findUser(Long id);

}