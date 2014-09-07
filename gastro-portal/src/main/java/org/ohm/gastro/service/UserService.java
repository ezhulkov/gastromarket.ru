package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService extends UserDetailsService {

    public List<UserEntity> findAllUser();

    void deleteUser(Long id);

    void saveUser(UserEntity user);

    UserEntity findUser(Long id);

}