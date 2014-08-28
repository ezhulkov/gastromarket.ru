package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserService {

    public List<UserEntity> findAllUser();

    public List<CatalogEntity> findAllCatalogs();

    public List<CatalogEntity> findAllCatalogs(UserEntity user);

    void deleteUser(Long id);

    void saveUser(UserEntity user);

    UserEntity findUser(Long id);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

}