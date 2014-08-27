package org.ohm.gastro.service;

import org.ohm.gastro.domain.CookEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService {

    public List<UserEntity> findAllUser();

    public List<CookEntity> findAllCooks();

    public List<CookEntity> findAllCooks(UserEntity user);

    void deleteUser(Long id);

    void saveUser(UserEntity user);

}

