package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CookEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CookRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CookRepository cookRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CookRepository cookRepository) {
        this.userRepository = userRepository;
        this.cookRepository = cookRepository;
    }

    @Override
    public List<UserEntity> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<CookEntity> findAllCooks() {
        return cookRepository.findAll();
    }

    @Override
    public List<CookEntity> findAllCooks(UserEntity user) {
        return cookRepository.findAllByUser(user);
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

}
