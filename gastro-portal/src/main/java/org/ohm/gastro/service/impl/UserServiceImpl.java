package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
    private final CatalogRepository catalogRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingService ratingService;
    private final MailService mailService;
    private final Random random = new Random();

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final CatalogRepository catalogRepository,
                           final OrderRepository orderRepository,
                           final PasswordEncoder passwordEncoder,
                           final RatingService ratingService,
                           final MailService mailService) {
        this.catalogRepository = catalogRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.mailService = mailService;
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
    public UserEntity createUser(UserEntity user, String password) throws UserExistsException, EmptyPasswordException {
        if (StringUtils.isNotEmpty(password)) user.setPassword(passwordEncoder.encode(password));
        if (user.getId() == null) {
            if (userRepository.findByEmail(user.getEmail()) != null) throw new UserExistsException();
            if (Type.COOK.equals(user.getType())) {
                CatalogEntity catalog = new CatalogEntity();
                catalog.setUser(user);
                catalog.setName("Страница кулинара '" + user.getFullName() + "'");
                catalogRepository.save(catalog);
                mailService.sendMailMessage(user.getEmail(), MailService.NEW_CATALOG, ImmutableMap.of("cook", user, "catalog", catalog, "password", password));
            } else if (Type.USER.equals(user.getType())) {
                mailService.sendMailMessage(user.getEmail(), MailService.NEW_USER, ImmutableMap.of("user", user, "password", password));
            }
        }
        return userRepository.save(user);
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
            logger.debug("Setting new password {} for user {}", password, user);
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

        mailService.sendAdminMessage(MailService.NEW_APPLICATION, ImmutableMap.of("fullname", defaultIfNull(fullName, ""),
                                                                                  "email", defaultIfNull(eMail, ""),
                                                                                  "about", defaultIfNull(about, "")));
        mailService.sendAdminMessage(MailService.NEW_APPLICATION_COOK, ImmutableMap.of("fullname", defaultIfNull(fullName, ""),
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
    public int getUserBonuses(final UserEntity user) {
        if (user.getType() == Type.USER) {
            final Integer userBonus = user.getBonus();
            final Integer usedBonus = orderRepository.findAllByCatalogAndCustomer(user, null).stream()
                    .filter(t -> t.getStatus() == OrderEntity.Status.NEW || t.getStatus() == OrderEntity.Status.ACCEPTED)
                    .collect(Collectors.summingInt(OrderEntity::getUsedBonuses));
            return Math.max(0, userBonus - usedBonus);
        }
        return 0;
    }

    @Override
    @RatingModifier
    public void afterSuccessfulLogin(@Nonnull @RatingTarget final UserDetails userDetails) {

        if (userDetails instanceof UserEntity) {
            UserEntity user = (UserEntity) userDetails;
            logger.info("User {} successful logged in", user);
            user.setLoginDate(new Date());
            saveUser(user);
            ratingService.registerEvent(LogEntity.Type.LOGIN, user);
        }

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
