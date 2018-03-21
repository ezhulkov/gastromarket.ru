package org.ohm.gastro.gui.pages;

import com.google.common.collect.Lists;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.dto.CarouselCatalogInfo;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private CarouselCatalogInfo carousel;

    @Property
    private int carouselIndex;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Override
    public String getTitle() {
        return getMessages().get("page.title");
    }

    public List<CarouselCatalogInfo> getCarouselInfo() {
        if (RegionFilter.getCurrentRegion() == Region.DEFAULT) {
            return Lists.newArrayList(
                    CarouselCatalogInfo.of("Кондитерская Фамм", "/catalog/konditerskaya-famm", "/img/main/mow/main3.jpg"),
                    CarouselCatalogInfo.of("\"Sunbeam\". Торты и сладости", "/catalog/sunbeam-torty-i-sladosti", "/img/main/mow/main6.jpg"),
                    CarouselCatalogInfo.of("Sweeteria Desserts", "/catalog/sweeteria-desserts", "/img/main/mow/main2.jpg"),
                    CarouselCatalogInfo.of("Sweet Ti Homebakery", "/catalog/sweet-ti-homebakery", "/img/main/mow/main1.jpg"),
                    CarouselCatalogInfo.of("Домашняя кондитерская \"Сладкий Порт\"", "/catalog/domashnyaya-konditerskaya-sladkii-port", "/img/main/mow/main4.jpg"),
                    CarouselCatalogInfo.of("Кондитерская \"ЕШь\"", "/catalog/konditerskaya-esh", "/img/main/mow/main5.jpg")
            );
        }
        return Lists.newArrayList(
                CarouselCatalogInfo.of("Кондитерская студия DesGateaux", "/catalog/konditerskaya-studiya-desgateaux", "/img/main/spb/main1.jpg")
        );
    }

}
