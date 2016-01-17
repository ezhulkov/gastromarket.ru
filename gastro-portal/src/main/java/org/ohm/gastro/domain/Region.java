package org.ohm.gastro.domain;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * Created by ezhulkov on 17.01.16.
 */
public enum Region {

    DEFAULT(true),
    SAINT_PETERSBURG("Saint Petersburg");

    private final String region;
    private final boolean defaultRegion;

    Region(final boolean defaultRegion) {
        this.defaultRegion = true;
        this.region = "Default";
    }

    Region(final String region) {
        this.region = region;
        this.defaultRegion = false;
    }

    public String getRegion() {
        return region;
    }

    public boolean isDefaultRegion() {
        return defaultRegion;
    }

    public static Region of(String region) {
        Preconditions.checkNotNull(region);
        return Arrays.stream(values()).filter(t -> t.getRegion().equals(region)).findFirst().orElse(Region.DEFAULT);
    }

    public static Region valueOfSafe(String region) {
        Preconditions.checkNotNull(region);
        return Arrays.stream(values()).filter(t -> t.name().equals(region)).findFirst().orElse(Region.DEFAULT);
    }

}
