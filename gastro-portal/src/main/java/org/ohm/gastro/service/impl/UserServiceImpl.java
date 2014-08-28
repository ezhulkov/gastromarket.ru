package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<CatalogEntity> findAllCatalogs() {
        return catalogRepository.findAll();
    }

    @Override
    public List<CatalogEntity> findAllCatalogs(UserEntity user) {
        return catalogRepository.findAllByUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    @Transactional
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    @Override
    public UserEntity findUser(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    @Transactional
    public void deleteCatalog(Long id) {
        catalogRepository.delete(id);
    }

    @Override
    @Transactional
    public void saveCatalog(CatalogEntity catalog) {
        catalogRepository.save(catalog);
    }

    @Override
    public CatalogEntity findCatalog(Long id) {
        return catalogRepository.findOne(id);
    }

}
