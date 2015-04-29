package org.ohm.gastro.service.impl;

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
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 29.04.15.
 */
@Component
@Transactional
public class RatingServiceImpl implements RatingService, Logging {

    private final LogRepository logRepository;
    private final OrderRepository orderRepository;
    private final RatingService ratingService;
    private final CommentRepository commentRepository;
    private final CatalogRepository catalogRepository;

    private final int historyDays;
    private final float retentionCoeff;
    private final float posRatingCoeff;
    private final float negRatingCoeff;
    private final float transactionCoeff;
    private final float productionCoeff;
    private final float productsCoeff;

    @Autowired
    public RatingServiceImpl(LogRepository logRepository,
                             OrderRepository orderRepository,
                             RatingService ratingService,
                             CommentRepository commentRepository,
                             CatalogRepository catalogRepository,
                             @Value("${rating.history.days}") int historyDays,
                             @Value("${rating.retention.coeff}") float retentionCoeff,
                             @Value("${rating.pos.rating.coeff}") float posRatingCoeff,
                             @Value("${rating.neg.rating.coeff}") float negRatingCoeff,
                             @Value("${rating.transaction.coeff}") float transactionCoeff,
                             @Value("${rating.production.coeff}") float productionCoeff,
                             @Value("${rating.products.coeff}") float productsCoeff) {
        this.logRepository = logRepository;
        this.orderRepository = orderRepository;
        this.ratingService = ratingService;
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.historyDays = historyDays;
        this.retentionCoeff = retentionCoeff;
        this.posRatingCoeff = posRatingCoeff;
        this.negRatingCoeff = negRatingCoeff;
        this.transactionCoeff = transactionCoeff;
        this.productionCoeff = productionCoeff;
        this.productsCoeff = productsCoeff;
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
    public void registerEvent(Type type, CatalogEntity catalog) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
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
    public void rateCatalog(final CatalogEntity catalog, final String text, final int rating, final UserEntity user) {

        if (StringUtils.isEmpty(text) || user == null) return;
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCatalog(catalog);
        commentEntity.setAuthor(user);
        commentEntity.setText(text);
        commentEntity.setDate(new Timestamp(System.currentTimeMillis()));
        commentEntity.setRating(rating);
        commentRepository.save(commentEntity);

    }

    @Override
    public void updateRating(final CatalogEntity catalog) {

        final Date fromDate = DateUtils.addDays(new Date(), -historyDays);
        final List<CommentEntity> ratings = commentRepository.findAllRatings(catalog);
        final List<LogEntity> catalogOps = ratingService.findEvents(catalog.getUser(), catalog, fromDate);

        final int productsCount = catalog.getReadyProducts().size();
        final int retentionCount = ratingService.findEvents(catalog.getUser(), fromDate, Type.LOGIN).size();
        final int posCount = (int) ratings.stream().filter(t -> t.getRating() > 0).count();
        final int negCount = (int) ratings.stream().filter(t -> t.getRating() < 0).count();
        final int totalSum = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).mapToLong(LogEntity::getCount).sum();
        final int doneOrdersCount = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).count();
        final int totalOrdersCount = orderRepository.findAllByCatalog(catalog, null).size();

        logger.info("Updating rating for {}", catalog);

        catalog.setRating(calcRating(productsCount, retentionCount, posCount, negCount, doneOrdersCount, totalOrdersCount, totalSum));

        catalogRepository.save(catalog);

    }

    @Override
    public List<CommentEntity> findAllComments(CatalogEntity catalog) {
        return commentRepository.findAllByCatalogOrderByIdDesc(catalog);
    }

    private int calcRating(final int productsCount, final int retentionCount, final int posCount, final int negCount, final int doneCount, final int allCount, final int totalSum) {
        return (int) (
                productsCount * productsCoeff +
                        retentionCount * retentionCoeff +
                        posCount * posRatingCoeff +
                        negCount * negRatingCoeff +
                        doneCount / allCount * productionCoeff +
                        totalSum * transactionCoeff
        );
    }

}
