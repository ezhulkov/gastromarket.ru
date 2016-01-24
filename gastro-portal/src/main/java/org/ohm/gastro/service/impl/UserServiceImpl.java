package org.ohm.gastro.service.impl;

import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.BillService;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.service.UserExistsException;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component("userService")
@Transactional
@ImageUploader(FileType.AVATAR)
public class UserServiceImpl implements UserService, Logging {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingService ratingService;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;
    private final Random random = new Random();
    private final BillService billService;

    private CatalogService catalogService;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final PasswordEncoder passwordEncoder,
                           final RatingService ratingService,
                           final CatalogRepository catalogRepository,
                           final MailService mailService,
                           final BillService billService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
        this.billService = billService;
    }

    @Autowired
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public List<UserEntity> findAllUser() {
        return userRepository.findAll(new Sort("fullName"));
    }

    @Override
    public List<UserEntity> findAllChildren(UserEntity referrer) {
        return userRepository.findAllByReferrer(referrer);
    }

    @Override
    public void toggleUser(Long id) {
        UserEntity user = userRepository.findOne(id);
        user.setStatus(user.getStatus() == Status.ENABLED ? Status.DISABLED : Status.ENABLED);
        userRepository.save(user);
    }

    @Override
    public UserEntity saveUser(final UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity saveUser(final UserEntity user, final String password) throws EmptyPasswordException {
        if (StringUtils.isNotEmpty(password)) user.setPassword(passwordEncoder.encode(password));
        if (user.getType() == Type.COOK) {
            mailService.syncChimpList(user, ImmutableMap.of(
                    MailService.MC_FNAME, user.getFullName() == null ? "" : user.getFullName().split("\\s")[0],
                    MailService.MC_REGION, user.getCatalogs().size() == 0 ? "" : ObjectUtils.defaultIfNull(user.getCatalogs().get(0).getRegion(), Region.DEFAULT).name(),
                    MailService.MC_CATALOG, user.getCatalogs().size() == 0 ? "" : user.getCatalogs().get(0).getFullUrl(),
                    MailService.MC_SOURCE, user.getSourceUrl() == null ? "" : user.getSourceUrl(),
                    MailService.MC_PASSWORD, password == null ? "" : password
            ));
        }
        return saveUser(user);
    }

    @Override
    public UserEntity createUser(final UserEntity user, final String password, String catalogName, Region region, final boolean sendEmail) throws UserExistsException, EmptyPasswordException {
        if (StringUtils.isEmpty(password)) throw new EmptyPasswordException();
        if (userRepository.findByEmail(user.getEmail()) != null && user.getType() != Type.COOK) throw new UserExistsException();
        user.setRegion(region);
        if (Type.COOK.equals(user.getType())) {
            userRepository.save(user);
            CatalogEntity catalog = new CatalogEntity();
            catalog.setUser(user);
            catalog.setRegion(region);
            catalog.setName(StringUtils.isEmpty(catalogName) ? user.getFullName() + " - страница кулинара" : catalogName);
            catalogService.saveWithAltId(catalog, catalogRepository);
            user.getCatalogs().add(catalog);
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("username", user.getFullName());
                    put("user", user);
                    put("catalog", catalog);
                    put("password", password);
                }
            };
            if (sendEmail) mailService.sendMailMessage(user, MailService.NEW_CATALOG, params);
        } else if (Type.USER.equals(user.getType())) {
            user.setBonus(100);
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("username", user.getFullName());
                    put("user", user);
                    put("password", password);
                }
            };
            if (sendEmail) mailService.sendMailMessage(user, MailService.NEW_USER, params);
        }
        user.setLoginDate(new Date());
        saveUser(user, password);
        return user;
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
            logger.info("Setting new password for user {}, {}", user, password);
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("username", ObjectUtils.defaultIfNull(user.getFullName(), ""));
                    put("password", password);
                }
            };
            mailService.sendMailMessage(eMail, MailService.CHANGE_PASSWD, params);
        }
    }

    @Override
    public void processApplicationRequest(String eMail, Region region, String fullName, String about, String sourceInfo) {

        logger.info("Application received");
        logger.info("region: {}", region);
        logger.info("email: {}", eMail);
        logger.info("fullName: {}", fullName);
        logger.info("about: {}", about);
        logger.info("sourceInfo: {}", sourceInfo);

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("cookname", defaultIfNull(fullName, ""));
                put("region", defaultIfNull(region, ""));
                put("email", defaultIfNull(eMail, ""));
                put("sourceInfo", defaultIfNull(sourceInfo, ""));
                put("about", defaultIfNull(about, ""));
                put("username", defaultIfNull(fullName, ""));
            }
        };
        mailService.sendAdminMessage(MailService.NEW_APPLICATION, params);
        mailService.sendMailMessage(eMail, MailService.NEW_APPLICATION_COOK, params);

    }

    @Override
    public void processFeedbackRequest(final String eMail, final String fullName, final String comment) {

        logger.info("Feedback received");
        logger.info("email: {}", eMail);
        logger.info("fullName: {}", fullName);
        logger.info("comment: {}", comment);

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("fullname", defaultIfNull(fullName, ""));
                put("email", defaultIfNull(eMail, ""));
                put("comment", defaultIfNull(comment, ""));
            }
        };
        mailService.sendAdminMessage(MailService.FEEDBACK, params);

    }

    @Override
    public void signupSocial(UserEntity userProfile, final Object referrerUser) {
        UserEntity existingUser = userRepository.findByEmail(userProfile.getEmail());
        if (referrerUser != null) userProfile.setReferrer(userRepository.findOne(((UserEntity) referrerUser).getId()));
        if (existingUser == null) {
            userProfile.setRegion(RegionFilter.getCurrentRegion());
            existingUser = userRepository.save(userProfile);
        } else {
            existingUser.setRegion(RegionFilter.getCurrentRegion());
            if (StringUtils.isEmpty(existingUser.getFullName())) existingUser.setFullName(userProfile.getFullName());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrl())) existingUser.setAvatarUrl(userProfile.getAvatarUrl());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrlMedium())) existingUser.setAvatarUrlMedium(userProfile.getAvatarUrlMedium());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrlSmall())) existingUser.setAvatarUrlSmall(userProfile.getAvatarUrlSmall());
            userRepository.save(existingUser);
        }
        manuallyLogin(existingUser);
    }

    @Override
    public void manuallyLogin(final UserEntity user) {
        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            afterSuccessfulLogin(user);
        }
    }

    @Override
    @RatingModifier
    public void afterSuccessfulLogin(@Nonnull @RatingTarget final UserDetails userDetails) {

        if (userDetails instanceof UserEntity) {
            UserEntity user = userRepository.findByEmail(((UserEntity) userDetails).getEmail());
            logger.info("User {} successfully logged in", user);
            user.setLoginDate(new Date());
            saveUser(user);
            ratingService.registerEvent(LogEntity.Type.LOGIN, user, null, null);
            if (user.isCook()) catalogRepository.findAllByUser(user).forEach(billService::createMissingBills);
        } else {
            logger.error("Not an UserEntity instance {}", userDetails);
        }

    }

    @Override
    public void importUsers(String csvUsers) {
        final CsvToBean csv = new CsvToBean() {
            @Override
            protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
                if (!StringUtils.isEmpty(value)) {
                    return super.convertValue(value, prop);
                }
                return null;
            }
        };
        final HeaderColumnNameMappingStrategy<UserImportDto> strat = new HeaderColumnNameMappingStrategy<>();
        strat.setType(UserImportDto.class);
        List<UserImportDto> result = csv.parse(strat, new StringReader(csvUsers));
        final int created = result.stream().mapToInt(user -> {
            logger.info("Importing user {}", user);
            if (userRepository.findByEmail(user.getEmail()) == null) {
                final UserEntity userEntity = new UserEntity();
                userEntity.setStatus(Status.ENABLED);
                userEntity.setType(Type.COOK);
                userEntity.setEmail(user.getEmail().toLowerCase());
                userEntity.setFullName(user.getName());
                userEntity.setSourceUrl(user.getSource().toLowerCase());
                try {
                    createUser(userEntity, user.getPassword(), user.getCatalog(), RegionFilter.getCurrentRegion(), false);
                } catch (Exception e) {
                    logger.error("", e);
                }
                return 1;
            }
            return 0;
        }).sum();
        logger.info("Users imported {}", created);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final UserEntity user = userRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException(email + " not found");
        return user;
    }

    @Override
    public void processUploadedImages(final FileType fileType, String objectId, Map<ImageSize, String> imageUrls) {
        checkNotNull(objectId, "ObjectId should not be null");
        UserEntity user = userRepository.findOne(Long.parseLong(objectId));
        checkNotNull(user, "User should not be null");
        user.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), user.getAvatarUrlSmall()));
        user.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), user.getAvatarUrlMedium()));
        user.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), user.getAvatarUrl()));
        userRepository.save(user);
    }

    @Override
    public String explicitlyGetObject(final FileType fileType, final String objectIdStr, final String targetContext, final UserEntity caller) {
        return caller.getId().toString();
    }

    @Override
    public void deleteUser(Long id) {
        final UserEntity user = userRepository.findOne(id);
        userRepository.delete(user);
        mailService.deleteChimpList(user);
    }

    @Override
    public UserEntity findUser(String email) {
        return userRepository.findByEmail(email);
    }

}
