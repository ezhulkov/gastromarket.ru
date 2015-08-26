package org.ohm.gastro.gui.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.ohm.gastro.gui.dto.ShoppingCart;

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
        configuration.add("/social/*");
        configuration.add("/upload/*");
        configuration.add("/favicon.ico");
        configuration.add("/robots.txt");
        configuration.add("/sitemap.xml");
    }

    public void contributeApplicationStateManager(MappedConfiguration<Class, ApplicationStateContribution> configuration) {
        configuration.add(ShoppingCart.class, new ApplicationStateContribution("session", ShoppingCart::new));
    }

}
