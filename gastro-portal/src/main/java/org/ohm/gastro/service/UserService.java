package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService extends UserDetailsService, ImageUploaderService<UserEntity, UserEntity> {

    List<UserEntity> findAllUser();

    List<UserEntity> findAllChildren(UserEntity referrer);

    void toggleUser(Long id);

    UserEntity saveUser(UserEntity user);

    UserEntity saveUser(UserEntity user, String password) throws EmptyPasswordException;

    UserEntity createUser(UserEntity user, String password, String catalogName, boolean sendEmail) throws UserExistsException, EmptyPasswordException;

    UserEntity findUser(Long id);

    void resetPassword(String eMail);

    void processApplicationRequest(String eMail, String fullName, String about, String sourceInfo);

    void processFeedbackRequest(String eMail, String fullName, String comment);

    void signupSocial(UserEntity userProfile, Object referrerUser);

    void afterSuccessfulLogin(@Nonnull UserDetails user);

    void importUsers(String csvUsers);

    void deleteUser(Long id);

}