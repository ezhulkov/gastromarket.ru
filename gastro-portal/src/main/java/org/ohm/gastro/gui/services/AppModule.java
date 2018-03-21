package org.ohm.gastro.gui.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStatePersistenceStrategy;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.ohm.gastro.gui.dto.BreadcrumbsList;
import org.ohm.gastro.gui.dto.ShoppingCart;
import org.ohm.gastro.gui.dto.TitleHolder;

/**
 * Created by ezhulkov on 21.08.14.
 */
public class AppModule {

    public static void contributeIgnoredPathsFilter(Configuration<String> configuration) {
        configuration.add("/js/.*");
        configuration.add("/css/.*");
        configuration.add("/img/.*");
        configuration.add("/favicon/.*");
        configuration.add("/fonts/.*");
        configuration.add("/sound/.*");
        configuration.add("/social/*");
        configuration.add("/upload/*");
        configuration.add("/payment/*");
        configuration.add("/chat/*");
        configuration.add("/message/*");
        configuration.add("/favicon.ico");
        configuration.add("/robots.txt");
        configuration.add("/sitemap.xml");
    }

    public void contributeApplicationStateManager(MappedConfiguration<Class, ApplicationStateContribution> configuration) {
        configuration.add(ShoppingCart.class, new ApplicationStateContribution("session", ShoppingCart::new));
        configuration.add(BreadcrumbsList.class, new ApplicationStateContribution("request", BreadcrumbsList::new));
        configuration.add(TitleHolder.class, new ApplicationStateContribution("request", TitleHolder::new));
    }

    public void contributeApplicationStatePersistenceStrategySource(MappedConfiguration<String, ApplicationStatePersistenceStrategy> configuration) {
        configuration.addInstance("request", RequestApplicationStatePersistenceStrategy.class);
    }

    @Contribute(MarkupRenderer.class)
    public static void deactiveDefaultCSS(OrderedConfiguration<MarkupRendererFilter> configuration) {
        configuration.override("InjectDefaultStylesheet", null);
    }

}
