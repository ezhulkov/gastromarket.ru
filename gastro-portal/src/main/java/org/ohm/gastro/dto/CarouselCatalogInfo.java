package org.ohm.gastro.dto;

/**
 * Created by ezhulkov on 28.05.16.
 */
public class CarouselCatalogInfo {

    private final String name;
    private final String url;
    private final String imageUrl;

    private CarouselCatalogInfo(final String name, final String url, final String imageUrl) {
        this.name = name;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public static CarouselCatalogInfo of(final String name, final String url, final String imageUrl) {
        return new CarouselCatalogInfo(name, url, imageUrl);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
