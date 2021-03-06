package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService, AltIdService<CatalogEntity, CatalogRepository> {

    int MAX_WIZARD_STEP = 4;

    List<CatalogEntity> findAllCatalogs();

    List<CatalogEntity> findAllActiveCatalogs(Region region);

    List<CatalogEntity> findAllCatalogs(UserEntity user);

    List<CatalogEntity> findAllCatalogs(Collection<Long> ids);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    CatalogEntity findCatalog(String altId);

    List<CatalogEntity> findDiscountCatalogs();

}
