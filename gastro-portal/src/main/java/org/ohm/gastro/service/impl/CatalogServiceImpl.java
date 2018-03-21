package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component("catalogService")
@Transactional
@ImageUploader(FileType.CATALOG)
public class CatalogServiceImpl implements CatalogService, Logging {

    private final CatalogRepository catalogRepository;
    private final MailService mailService;

    @Autowired
    public CatalogServiceImpl(CatalogRepository catalogRepository, MailService mailService) {
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
    }

    @Override
    public List<CatalogEntity> findAllCatalogs() {
        return catalogRepository.findAll();
    }

    @Override
    public List<CatalogEntity> findAllActiveCatalogs(Region region) {
        return catalogRepository.findAllActive(region);
    }

    @Override
    public List<CatalogEntity> findAllCatalogs(UserEntity user) {
        if (user == null) return Lists.newArrayList();
        return catalogRepository.findAllByUser(user);
    }

    @Override
    public List<CatalogEntity> findAllCatalogs(final Collection<Long> ids) {
        return catalogRepository.findAll(ids);
    }

    @Override
    public void deleteCatalog(Long id) {
        catalogRepository.delete(id);
    }

    @Override
    public void saveCatalog(CatalogEntity catalog) {
        catalog.setLastModified(new Date());
        saveWithAltId(catalog, catalogRepository);
        final UserEntity user = catalog.getUser();
        mailService.syncChimpList(user.getEmail(), ImmutableMap.of(
                MailService.MC_FILLED, Boolean.valueOf(catalog.isWasSetup()).toString(),
                MailService.MC_REGION, ObjectUtils.defaultIfNull(catalog.getRegion(), Region.DEFAULT).name(),
                MailService.MC_CATALOG, catalog.getFullUrl()
        ), MailService.MC_COOKS_LIST);
    }

    @Override
    public CatalogEntity findCatalog(Long id) {
        return catalogRepository.findOne(id);
    }

    @Override
    public CatalogEntity findCatalog(final String altId) {
        return findByAltId(altId, catalogRepository);
    }

    @Override
    public List<CatalogEntity> findDiscountCatalogs() {
        return catalogRepository.findDiscountCatalogs();
    }

    @Override
    public void processUploadedImages(final FileType fileType, String objectId, Map<ImageSize, String> imageUrls) {
        checkNotNull(objectId, "ObjectId should not be null");
        CatalogEntity catalog = catalogRepository.findOne(Long.parseLong(objectId));
        checkNotNull(catalog, "Product should not be null");
        catalog.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), catalog.getAvatarUrlSmall()));
        catalog.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), catalog.getAvatarUrlMedium()));
        catalog.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), catalog.getAvatarUrl()));
        catalog.setAvatarUrlBig(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE4), catalog.getAvatarUrl()));
        catalogRepository.saveAndFlush(catalog);
    }

}
