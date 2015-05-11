package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService<CatalogEntity>, AltIdService<CatalogEntity, CatalogRepository> {

    public static final int MAX_WIZARD_STEP = 4;

    List<PropertyEntity> findAllProperties();

    List<PropertyValueEntity> findAllValues(PropertyEntity property);

    PropertyEntity findProperty(Long id);

    PropertyValueEntity findPropertyValue(Long id);

    PropertyEntity saveProperty(PropertyEntity property);

    PropertyValueEntity savePropertyValue(PropertyValueEntity value);

    void deleteProperty(Long id);

    void deletePropertyValue(Long id);

    List<CatalogEntity> findAllCatalogs();

    List<CatalogEntity> findAllActiveCatalogs();

    List<CatalogEntity> findAllCatalogs(UserEntity user);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    CatalogEntity findCatalog(String altId);

}
