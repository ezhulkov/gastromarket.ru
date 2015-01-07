package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService extends UserDetailsService {

    public List<UserEntity> findAllUser();

    void toggleUser(Long id);

    UserEntity saveUser(UserEntity user) throws UserExistsException, EmptyPasswordException;

    UserEntity findUser(Long id);

    void resetPassword(String eMail);

    void processApplicationRequest(String eMail, String fullName, String about);

}