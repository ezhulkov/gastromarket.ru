package org.ohm.gastro.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CatalogRepository catalogRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CatalogRepository catalogRepository) {
        this.userRepository = userRepository;
        this.catalogRepository = catalogRepository;
    }

    @Override
    public List<UserEntity> findAllUser() {
        return userRepository.findAll(new Sort("username"));
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
            if (userRepository.findByUsername(user.getUsername()) != null) throw new UserExistsException();
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

}
