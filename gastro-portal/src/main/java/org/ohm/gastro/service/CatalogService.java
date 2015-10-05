package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService<CatalogEntity, CatalogEntity>, AltIdService<CatalogEntity, CatalogRepository> {

    int MAX_WIZARD_STEP = 4;

    List<CatalogEntity> findAllCatalogs();

    List<CatalogEntity> findAllActiveCatalogs();

    List<CatalogEntity> findAllCatalogs(UserEntity user);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    CatalogEntity findCatalog(String altId);

}
