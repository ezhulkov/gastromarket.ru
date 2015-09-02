package org.ohm.gastro.service.impl;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ohm.gastro.domain.OrderEntity.Type;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OfferRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component
public class SitemapService implements Runnable, Logging {

    private final CatalogRepository catalogRepository;
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final String sitemapPath;
    private final boolean production;
    private final VelocityEngine velocityEngine;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public SitemapService(final CatalogRepository catalogRepository,
                          final OfferRepository offerRepository,
                          final OrderRepository orderRepository,
                          final ProductRepository productRepository,
                          final @Value("${sitemap.path}") String sitemapPath,
                          final @Value("${production}") boolean production) {
        final Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty("velocimacro.library", "");
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        properties.put("runtime.log.logsystem.log4j.category", "velocity");
        properties.put("runtime.log.logsystem.log4j.logger", "velocity");
        this.velocityEngine = new VelocityEngine(properties);
        this.catalogRepository = catalogRepository;
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.sitemapPath = sitemapPath;
        this.production = production;
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.DAYS);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        if (!production) return;
        try (
                final Writer stringWriter = new FileWriter(sitemapPath + File.separator + "sitemap.xml")
        ) {
            logger.info("Building sitemap");
            final Map<String, Object> params = new HashMap<>();
            params.put("catalogs", catalogRepository.findAllActive());
            params.put("tenders", orderRepository.findAllByType(Type.PUBLIC));
            params.put("products", productRepository.findAll().stream().filter(ProductEntity::isWasSetup).collect(Collectors.toList()));
            params.put("offers", offerRepository.findAll());
            velocityEngine.mergeTemplate("sitemap.vm", "UTF-8", new VelocityContext(params), stringWriter);
            logger.info("Sitemap done");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
