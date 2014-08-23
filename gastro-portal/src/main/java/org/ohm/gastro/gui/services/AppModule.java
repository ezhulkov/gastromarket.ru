package org.ohm.gastro.gui.services;

import org.apache.tapestry5.ioc.Configuration;

/**
 * Created by ezhulkov on 21.08.14.
 */
public class AppModule {

    public static void contributeIgnoredPathsFilter(Configuration<String> configuration) {
        configuration.add("/js/.*");
        configuration.add("/css/.*");
        configuration.add("/img/.*");
        configuration.add("/fonts/.*");
    }

}
