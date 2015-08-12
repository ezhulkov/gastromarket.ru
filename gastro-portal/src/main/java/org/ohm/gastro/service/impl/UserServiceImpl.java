package org.ohm.gastro.service.impl;

import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
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
import org.springframework.security.core.Authentication;
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
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.apache.commons.lang.ObjectUtils.defaultIfNull;
import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Component("userService")
@Transactional
@ImageUploader(FileType.AVATAR)
public class UserServiceImpl implements UserService, Logging {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingService ratingService;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;
    private final Random random = new Random();

    private CatalogService catalogService;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final OrderRepository orderRepository,
                           final PasswordEncoder passwordEncoder,
                           final RatingService ratingService,
                           final CatalogRepository catalogRepository,
                           final MailService mailService) {
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
    }

    @Autowired
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public List<UserEntity> findAllUser() {
        return userRepository.findAll(new Sort("email"));
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
            //send to mailchimp
            mailService.syncChimpList(user, ImmutableMap.of(
                    MailService.MC_FNAME, user.getFullName() == null ? "" : user.getFullName().split("\\s")[0],
                    MailService.MC_CATALOG, user.getCatalogs().size() == 0 ? "" : user.getCatalogs().get(0).getFullUrl(),
                    MailService.MC_SOURCE, user.getSourceUrl() == null ? "" : user.getSourceUrl(),
                    MailService.MC_PASSWORD, password == null ? "" : password
            ));
        }
        return saveUser(user);
    }

    @Override
    public UserEntity createUser(final UserEntity user, final String password, String catalogName, final boolean sendEmail) throws UserExistsException, EmptyPasswordException {
        if (StringUtils.isEmpty(password)) throw new EmptyPasswordException();
        if (userRepository.findByEmail(user.getEmail()) != null) throw new UserExistsException();
        if (Type.COOK.equals(user.getType())) {
            CatalogEntity catalog = new CatalogEntity();
            catalog.setUser(user);
            catalog.setName(StringUtils.isEmpty(catalogName) ? user.getFullName() + " - страница кулинара" : catalogName);
            catalogService.saveWithAltId(catalog, catalogRepository);
            user.getCatalogs().add(catalog);
            if (sendEmail) mailService.sendMailMessage(user.getEmail(),
                                                       MailService.NEW_CATALOG,
                                                       ImmutableMap.of("username", user.getFullName(),
                                                                       "cook", user,
                                                                       "catalog", catalog,
                                                                       "password", password));
        } else if (Type.USER.equals(user.getType())) {
            if (sendEmail) mailService.sendMailMessage(user.getEmail(),
                                                       MailService.NEW_USER,
                                                       ImmutableMap.of("username", user.getFullName(),
                                                                       "user", user,
                                                                       "password", password));
        }
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
            logger.info("Setting new password {} for user {}", password, user);
            mailService.sendMailMessage(eMail, MailService.CHANGE_PASSWD, ImmutableMap.of("username", user.getFullName(),
                                                                                          "password", password));
        }
    }

    @Override
    public void processApplicationRequest(String eMail, String fullName, String about) {

        logger.info("Application received");
        logger.info("email: {}", eMail);
        logger.info("fullName: {}", fullName);
        logger.info("about: {}", about);

        mailService.sendAdminMessage(MailService.NEW_APPLICATION, ImmutableMap.of("username", defaultIfNull(fullName, ""),
                                                                                  "email", defaultIfNull(eMail, ""),
                                                                                  "about", defaultIfNull(about, "")));
        mailService.sendMailMessage(eMail, MailService.NEW_APPLICATION_COOK, ImmutableMap.of("username", defaultIfNull(fullName, ""),
                                                                                             "email", defaultIfNull(eMail, ""),
                                                                                             "about", defaultIfNull(about, "")));

    }

    @Override
    public void processFeedbackRequest(final String eMail, final String fullName, final String comment) {

        logger.info("Feedback received");
        logger.info("email: {}", eMail);
        logger.info("fullName: {}", fullName);
        logger.info("comment: {}", comment);

        mailService.sendAdminMessage(MailService.FEEDBACK, ImmutableMap.of("fullname", defaultIfNull(fullName, ""),
                                                                           "email", defaultIfNull(eMail, ""),
                                                                           "comment", defaultIfNull(comment, "")));

    }

    @Override
    public void signupSocial(UserEntity userProfile) {

        UserEntity existingUser = userRepository.findByEmail(userProfile.getEmail());
        if (existingUser == null) {
            existingUser = userRepository.save(userProfile);
        } else {
            if (StringUtils.isEmpty(existingUser.getFullName())) existingUser.setFullName(userProfile.getFullName());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrl())) existingUser.setAvatarUrl(userProfile.getAvatarUrl());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrlMedium())) existingUser.setAvatarUrlMedium(userProfile.getAvatarUrlMedium());
            if (StringUtils.isNotEmpty(userProfile.getAvatarUrlSmall())) existingUser.setAvatarUrlSmall(userProfile.getAvatarUrlSmall());
            userRepository.save(existingUser);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(existingUser, null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        afterSuccessfulLogin(userProfile);

    }

    @Override
    @RatingModifier
    public void afterSuccessfulLogin(@Nonnull @RatingTarget final UserDetails userDetails) {

        if (userDetails instanceof UserEntity) {
            UserEntity user = userRepository.findByEmail(((UserEntity) userDetails).getEmail());
            logger.info("User {} successful logged in", user);
            user.setLoginDate(new Date());
            saveUser(user);
            ratingService.registerEvent(LogEntity.Type.LOGIN, user);
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
        result.forEach(user -> {
            logger.info("Importing user {}", user);
            if (userRepository.findByEmail(user.getEmail()) == null) {
                final UserEntity userEntity = new UserEntity();
                userEntity.setStatus(Status.ENABLED);
                userEntity.setType(Type.COOK);
                userEntity.setEmail(user.getEmail().toLowerCase());
                userEntity.setFullName(user.getName());
                userEntity.setSourceUrl(user.getSource().toLowerCase());
                try {
                    createUser(userEntity, user.getPassword(), user.getCatalog(), false);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        UserEntity user = userRepository.findOne(Long.parseLong(objectId));
        checkNotNull(user, "User should not be null");

        user.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), user.getAvatarUrlSmall()));
        user.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), user.getAvatarUrlMedium()));
        user.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), user.getAvatarUrl()));

        return userRepository.save(user);

    }

}
