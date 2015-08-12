package org.ohm.gastro.service.impl;

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
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.LogRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 29.04.15.
 */
@Component("ratingService")
@Transactional
public class RatingServiceImpl implements RatingService, Logging {

    private final LogRepository logRepository;
    private final OrderRepository orderRepository;
    private final CommentRepository commentRepository;
    private final CatalogRepository catalogRepository;
    private final ProductRepository productRepository;

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
    public RatingServiceImpl(LogRepository logRepository,
                             OrderRepository orderRepository,
                             CommentRepository commentRepository,
                             CatalogRepository catalogRepository,
                             ProductRepository productRepository,
                             @Value("${rating.series}") String ratingSeries,
                             @Value("${rank.badge.series}") String badgeRankSeries,
                             @Value("${product.badge.series}") String badgeProductSeries,
                             @Value("${order.badge.series}") String badgeOrderSeries,
                             @Value("${rating.history.days}") int historyDays,
                             @Value("${rating.retention.coeff}") float retentionCoeff,
                             @Value("${rating.pos.rating.coeff}") float posRatingCoeff,
                             @Value("${rating.neg.rating.coeff}") float negRatingCoeff,
                             @Value("${rating.transaction.coeff}") float transactionCoeff,
                             @Value("${rating.production.coeff}") float productionCoeff,
                             @Value("${rating.products.coeff}") float productsCoeff) {
        this.logRepository = logRepository;
        this.orderRepository = orderRepository;
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.productRepository = productRepository;
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
    public void registerEvent(Type type, UserEntity user) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
        log.setUser(user);
        logRepository.save(log);
    }

    @Override
    public void registerEvent(Type type, CatalogEntity catalog, long count) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
        log.setCount(count);
        log.setUser(catalog.getUser());
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
    public void rateCatalog(@RatingTarget final CatalogEntity catalog, final String text, final int rating, final UserEntity user) {

        if (StringUtils.isEmpty(text) || user == null) return;
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCatalog(catalog);
        commentEntity.setAuthor(user);
        commentEntity.setText(text);
        commentEntity.setDate(new Date());
        commentEntity.setRating(rating);
        commentRepository.save(commentEntity);

    }

    @Override
    public void updateRating(CatalogEntity catalog) {

        catalog = catalogRepository.findOne(catalog.getId());

        final Date fromDate = DateUtils.addDays(new Date(), -historyDays);
        final List<CommentEntity> ratings = commentRepository.findAllRatings(catalog);
        final List<LogEntity> catalogOps = findEvents(catalog.getUser(), catalog, fromDate);

        final int productsCount = productRepository.findAllByWasSetupAndCatalog(true, catalog).size();
        final int retentionCount = findEvents(catalog.getUser(), fromDate, Type.LOGIN).size();
        final int posCount = (int) ratings.stream().filter(t -> t.getRating() > 0).count();
        final int negCount = (int) ratings.stream().filter(t -> t.getRating() < 0).count();
        final int totalSum = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).mapToLong(LogEntity::getCount).sum();
        final int doneOrdersCount = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).count();
        final int totalOrdersCount = orderRepository.findAllByCatalog(catalog).size();

        final Integer prevLevel = catalog.getLevel();
        catalog.setRating(calcRating(productsCount, retentionCount, posCount, negCount, doneOrdersCount, totalOrdersCount, totalSum));
        catalog.setLevel(levelMap.get(catalog.getRating()));
        logger.info("Rating for catalog {} changed", catalog);

        if (!catalog.getLevel().equals(prevLevel)) registerEvent(Type.RATING_CHANGE, catalog, catalog.getLevel());

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

    }

    @Override
    public List<CommentEntity> findAllComments(CatalogEntity catalog) {
        return commentRepository.findAllByCatalogOrderByIdDesc(catalog);
    }

    private int calcRating(final int productsCount, final int retentionCount, final int posCount, final int negCount, final int doneCount, final int allCount, final int totalSum) {
        return (int) Math.max(0, productsCount * productsCoeff +
                                      retentionCount * retentionCoeff +
                                      posCount * posRatingCoeff +
                                      negCount * negRatingCoeff +
                                      (allCount == 0 ? 0 : (doneCount / allCount * productionCoeff)) +
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

}
