package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component("userService")
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
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    @Transactional
    public void saveUser(UserEntity user) {
        if (Type.COOK.equals(user.getType()) && user.getId() == null) {
            CatalogEntity catalog = new CatalogEntity();
            catalog.setWasSetup(false);
            catalog.setUser(user);
            catalog.setName("Каталог");
            catalogRepository.save(catalog);
        }
        userRepository.save(user);
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
