package org.ohm.gastro.service;

import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MailService.MailType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService extends UserDetailsService, ImageUploaderService {

    List<UserEntity> findAllUser();

    List<UserEntity> findAllChildren(UserEntity referrer);

    void toggleUser(Long id);

    void toggleSubscription(UserEntity user);

    void toggleSubscription(UserEntity user, Collection<MailType> disabledConfigs);

    UserEntity saveUser(UserEntity user);

    UserEntity saveUser(UserEntity user, String password) throws EmptyPasswordException;

    UserEntity createUser(UserEntity user, String password, String catalogName, Region region, boolean sendEmail) throws UserExistsException, EmptyPasswordException;

    UserEntity findUser(Long id);

    UserEntity findUser(String email);

    void resetPassword(String eMail);

    void processApplicationRequest(String eMail, Region region, String fullName, String about, String sourceInfo);

    void processFeedbackRequest(String eMail, String fullName, String comment);

    void signupSocial(UserEntity userProfile, Object referrerUser);

    void afterSuccessfulLogin(@Nonnull UserDetails user);

    void importUsers(String csvUsers, final Region region, boolean sendEmail);

    void deleteUser(Long id);

    void manuallyLogin(UserEntity user);

}