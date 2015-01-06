package org.ohm.gastro.service.impl;

import com.google.common.io.BaseEncoding;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService, Logging {

    private final UserRepository userRepository;
    private final CatalogRepository catalogRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CatalogRepository catalogRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.catalogRepository = catalogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> findAllUser() {
        return userRepository.findAll(new Sort("email"));
    }

    @Override
    public void toggleUser(Long id) {
        UserEntity user = userRepository.findOne(id);
        user.setStatus(user.getStatus() == Status.ENABLED ? Status.DISABLED : Status.ENABLED);
        userRepository.save(user);
    }

    @Override
    public UserEntity saveUser(UserEntity user) throws UserExistsException, EmptyPasswordException {
        if (StringUtils.isEmpty(user.getPassword())) throw new EmptyPasswordException();
        if (user.getId() == null) {
            if (userRepository.findByEmail(user.getEmail()) != null) throw new UserExistsException();
            if (Type.COOK.equals(user.getType())) {
                CatalogEntity catalog = new CatalogEntity();
                catalog.setWasSetup(false);
                catalog.setUser(user);
                catalog.setName("Каталог");
                catalogRepository.save(catalog);
            }
        }
        return userRepository.save(user);
    }

    @Override
    public UserEntity findUser(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public void resetPassword(String eMail) {
        //Generate
        UserEntity user = userRepository.findByEmail(eMail);
        if (user != null) {
            final byte[] buffer = new byte[5];
            random.nextBytes(buffer);
            String password = BaseEncoding.base64Url().omitPadding().encode(buffer);
            String encPassword = passwordEncoder.encode(password);
            user.setPassword(encPassword);
            userRepository.save(user);
            logger.debug("Setting new password {} for user {}", password, user);
            //todo
            //Send email
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

}
