package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableRangeSet.Builder;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.ImageRepository;
import org.ohm.gastro.reps.LogRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 29.04.15.
 */
@Component("ratingService")
@Transactional
@ImageUploader(FileType.COMMENT)
public class RatingServiceImpl implements RatingService, Logging {

    private final LogRepository logRepository;
    private final OrderRepository orderRepository;
    private final CommentRepository commentRepository;
    private final CatalogRepository catalogRepository;
    private final ImageRepository photoRepository;
    private final ProductRepository productRepository;
    private final MailService mailService;


    private final int historyDays;
    private final float retentionCoeff;
    private final float posRatingCoeff;
    private final float negRatingCoeff;
    private final float transactionCoeff;
    private final float productionCoeff;
    private final float productsCoeff;
    private final RangeMap<Integer, Integer> levelMap;
    private final RangeSet<Integer> rankBadgeSet;
    private final RangeSet<Integer> orderBadgeSet;
    private final RangeSet<Integer> productBadgeSet;

    @Autowired
    public RatingServiceImpl(final LogRepository logRepository,
                             final OrderRepository orderRepository,
                             final CommentRepository commentRepository,
                             final CatalogRepository catalogRepository,
                             final ProductRepository productRepository,
                             final ImageRepository photoRepository,
                             final MailService mailService,
                             final @Value("${rating.series}") String ratingSeries,
                             final @Value("${rank.badge.series}") String badgeRankSeries,
                             final @Value("${product.badge.series}") String badgeProductSeries,
                             final @Value("${order.badge.series}") String badgeOrderSeries,
                             final @Value("${rating.history.days}") int historyDays,
                             final @Value("${rating.retention.coeff}") float retentionCoeff,
                             final @Value("${rating.pos.rating.coeff}") float posRatingCoeff,
                             final @Value("${rating.neg.rating.coeff}") float negRatingCoeff,
                             final @Value("${rating.transaction.coeff}") float transactionCoeff,
                             final @Value("${rating.production.coeff}") float productionCoeff,
                             final @Value("${rating.products.coeff}") float productsCoeff) {
        this.logRepository = logRepository;
        this.orderRepository = orderRepository;
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.productRepository = productRepository;
        this.mailService = mailService;
        this.photoRepository = photoRepository;
        this.historyDays = historyDays;
        this.retentionCoeff = retentionCoeff;
        this.posRatingCoeff = posRatingCoeff;
        this.negRatingCoeff = negRatingCoeff;
        this.transactionCoeff = transactionCoeff;
        this.productionCoeff = productionCoeff;
        this.productsCoeff = productsCoeff;
        this.levelMap = TreeRangeMap.create();
        this.rankBadgeSet = toRangeSet(badgeRankSeries, true);
        this.orderBadgeSet = toRangeSet(badgeOrderSeries, false);
        this.productBadgeSet = toRangeSet(badgeProductSeries, false);
        int prevRange = 0;
        int level = 1;
        for (Integer range : Arrays.stream(ratingSeries.split(",")).map(Integer::parseInt).collect(Collectors.toList())) {
            this.levelMap.put(Range.openClosed(prevRange, range), level++);
            prevRange = range;
        }
    }

    @Override
    public void registerEvent(@Nonnull Type type, @Nonnull UserEntity user, @Nullable CatalogEntity catalog, @Nullable Integer data) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
        log.setCount(data);
        log.setUser(user);
        log.setCatalog(catalog);
        logRepository.save(log);
    }

    @Override
    public List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom) {
        return logRepository.findAll(user, catalog, dateFrom);
    }

    @Override
    public List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type) {
        return logRepository.findAll(user, dateFrom, type);
    }

    @Override
    @RatingModifier
    public void rateCatalog(@RatingTarget final CatalogEntity catalog, final String comment, final int rating, final UserEntity author) {
        if (StringUtils.isEmpty(comment) || author == null) return;
        final CommentEntity commentEntity = new CommentEntity();
        commentEntity.setType(CommentEntity.Type.CATALOG);
        commentEntity.setCatalog(catalog);
        commentEntity.setAuthor(author);
        commentEntity.setText(comment);
        commentEntity.setDate(new Date());
        commentEntity.setRating(rating);
        commentRepository.save(commentEntity);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", catalog.getFullUrl());
                put("text", comment);
                put("username", catalog.getUser().getFullName());
                put("catalog", catalog.getId());
            }
        };
        mailService.sendMailMessage(catalog.getUser().getEmail(), MailService.CATALOG_RATE, params);
    }

    @Override
    public void rateClient(final UserEntity user, final String comment, final int rating, final UserEntity author) {
        if (StringUtils.isEmpty(comment) || user == null) return;
        final CommentEntity commentEntity = new CommentEntity();
        commentEntity.setType(CommentEntity.Type.CUSTOMER);
        commentEntity.setUser(user);
        commentEntity.setAuthor(author);
        commentEntity.setText(comment);
        commentEntity.setDate(new Date());
        commentEntity.setRating(rating);
        commentRepository.save(commentEntity);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", user.getFullUrl());
                put("text", comment);
                put("username", user.getFullName());
                put("user", user);
            }
        };
        mailService.sendMailMessage(user.getEmail(), MailService.USER_RATE, params);
    }

    @Override
    public void updateRating(CatalogEntity catalog) {

        catalog = catalogRepository.findOne(catalog.getId());
        MDC.put("uid", catalog.getUser().getEmail());

        try {

            final Date fromDate = DateUtils.addDays(new Date(), -historyDays);
            final List<CommentEntity> ratings = commentRepository.findAllRatings(catalog);
            final List<LogEntity> catalogOps = findEvents(catalog.getUser(), catalog, fromDate);
            final List<OrderEntity> orders = orderRepository.findAllByCatalog(catalog);

            final int productsCount = productRepository.findAllByWasSetupAndCatalog(true, catalog).size();
            final int retentionCount = findEvents(catalog.getUser(), fromDate, Type.LOGIN).size();
            final int posCount = (int) ratings.stream().filter(t -> t.getRating() > 0).count();
            final int negCount = (int) ratings.stream().filter(t -> t.getRating() < 0).count();
            final int totalSum = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).mapToLong(LogEntity::getCount).sum();
            final int doneOrdersCount = (int) orders.stream().filter(t -> t.getStatus() == Status.CLOSED).count();
            final int cancelOrdersCount = (int) orders.stream().filter(t -> t.getStatus() == Status.CANCELLED).count();

            final Integer prevLevel = catalog.getLevel();
            final int rating = calcRating(productsCount, retentionCount, posCount, negCount, doneOrdersCount, cancelOrdersCount, totalSum);
            logger.info("productsCount:{}, retentionCount:{}, posCount:{}, negCount:{}, doneOrdersCount:{}, cancelOrdersCount:{}, totalSum:{}",
                        productsCount, retentionCount, posCount, negCount, doneOrdersCount, cancelOrdersCount, totalSum);

            if (catalog.getUser().getEmail().equals("cook@cook.com")) return;

            catalog.setRating(rating);
            catalog.setLevel(levelMap.get(rating));

            logger.info("Rating for catalog {} changed", catalog);

            if (catalog.getLevel() != null && (prevLevel == null || !catalog.getLevel().equals(prevLevel))) {
                registerEvent(Type.RATING_CHANGE, catalog.getUser(), catalog, catalog.getLevel());
            }

            catalog.setOrderBadge(orderBadgeSet.rangeContaining(doneOrdersCount).lowerEndpoint());
            catalog.setProductBadge(productBadgeSet.rangeContaining(productsCount).lowerEndpoint());
            catalogRepository.save(catalog);

            final List<CatalogEntity> catalogs = catalogRepository.findAllActive();
            int pos = 1;
            for (CatalogEntity oneCatalog : catalogs) {
                final Range<Integer> rankRange = rankBadgeSet.rangeContaining(pos++);
                if (rankRange == null) break;
                oneCatalog.setRankBadge(rankRange.upperEndpoint());
                catalogRepository.save(oneCatalog);
            }

        } finally {
            MDC.clear();
        }

    }

    @Override
    public List<CommentEntity> findAllComments(CatalogEntity catalog) {
        return commentRepository.findAllByCatalogOrderByIdDesc(catalog);
    }

    @Override
    public List<CommentEntity> findAllComments(final UserEntity customer) {
        return commentRepository.findAllByUserOrderByIdDesc(customer);
    }

    @Override
    public List<CommentEntity> findAllComments(final OrderEntity order) {
        return commentRepository.findAllByOrderOrderByIdDesc(order);
    }

    @Override
    public List<CommentEntity> findAllComments(final CommentEntity comment) {
        return commentRepository.findAllChildren(comment);
    }

    @Override
    public void createOrderComment(final OrderEntity order, final UserEntity author, final String replyText) {
        final CommentEntity reply = new CommentEntity();
        reply.setType(CommentEntity.Type.ORDER);
        reply.setOrder(order);
        reply.setAuthor(author);
        reply.setText(replyText);
        reply.setDate(new Date());
        commentRepository.save(reply);
    }

    @Override
    public void placeReply(final OrderEntity order, final UserEntity author, final String replyText) {
        if (author.isCook() && StringUtils.isNotEmpty(replyText) && findAllComments(order).stream().filter(t -> t.getAuthor().equals(author)).count() == 0) {
            try {
                createOrderComment(order, author, replyText);
                final Map<String, Object> params = new HashMap<String, Object>() {
                    {
                        put("username", order.getCustomer().getFullName());
                        put("address", order.getOrderUrl());
                        put("text", replyText);
                    }
                };
                mailService.sendMailMessage(order.getCustomer(), MailService.ORDER_COMMENT, params);
            } catch (MailException e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void placeReply(final CommentEntity comment, final UserEntity author, final String replyText) {
        final CommentEntity reply = new CommentEntity();
        final OrderEntity order = comment.getOrder();
        reply.setType(CommentEntity.Type.ORDER);
        reply.setParent(comment);
        reply.setAuthor(author);
        reply.setText(replyText);
        reply.setDate(new Date());
        commentRepository.save(reply);
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", order.getOrderUrl());
                    put("text", replyText);
                    put("order", order);
                }
            };
            if (!order.getCustomer().equals(author)) {
                params.put("username", order.getCustomer().getFullName());
                mailService.sendMailMessage(order.getCustomer(), MailService.ORDER_COMMENT, params);
            } else {
                params.put("username", comment.getAuthor().getFullName());
                mailService.sendMailMessage(comment.getAuthor(), MailService.ORDER_COMMENT, params);
            }
        } catch (MailException e) {
            logger.error("", e);
        }
    }

    @Override
    public List<PhotoEntity> findAllPhotos(CommentEntity comment) {
        return photoRepository.findAllByComment(comment);
    }

    @Override
    public List<CommentEntity> findAllComments(final OrderEntity order, final UserEntity author) {
        return commentRepository.findAllByOrderAndAuthorOrderByIdDesc(order, author);
    }

    @Override
    public void saveComment(CommentEntity comment) {
        commentRepository.save(comment);
    }

    @Override
    public CommentEntity findComment(final Long cId) {
        return commentRepository.findOne(cId);
    }

    private int calcRating(final int productsCount, final int retentionCount, final int posCount, final int negCount, final int doneOrdersCount, final int cancelOrdersCount, final int totalSum) {
        return (int) Math.max(0, productsCount * productsCoeff +
                                      retentionCount * retentionCoeff +
                                      posCount * posRatingCoeff +
                                      negCount * negRatingCoeff +
                                      doneOrdersCount / (cancelOrdersCount == 0 ? 1 : cancelOrdersCount) * productionCoeff +
                                      totalSum * transactionCoeff
        );
    }

    private RangeSet<Integer> toRangeSet(String series, boolean top) {
        int prevRange = 0;
        final Builder<Integer> builder = ImmutableRangeSet.builder();
        for (Integer range : Arrays.stream(series.split(",")).map(Integer::parseInt).collect(Collectors.toList())) {
            if (top) builder.add(Range.closed(prevRange + 1, range));
            else builder.add(Range.closed(prevRange, range - 1));
            prevRange = range;
        }
        if (!top) builder.add(Range.closed(prevRange, Integer.MAX_VALUE));
        return builder.build();
    }

    @Override
    public CommentEntity processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        final CommentEntity comment = commentRepository.findOne(Long.parseLong(objectId));
        checkNotNull(comment, "Comment should not be null");

        final PhotoEntity photo = new PhotoEntity();
        photo.setType(PhotoEntity.Type.COMMENT);
        photo.setComment(comment);
        photo.setUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), photo.getUrlSmall()));
        photo.setUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), photo.getUrl()));
        photoRepository.save(photo);

        return comment;
    }

}
