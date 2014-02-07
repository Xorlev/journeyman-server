package com.xorlev.journeyman.service.api;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.spatial4j.core.distance.DistanceUtils;
import com.xorlev.journeyman.service.core.HaversineCalculator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 2013-12-04
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
public class TripSegment {
    @Getter(AccessLevel.NONE) List<LocationPoint> locationPoints;

    public TripSegment(List<LocationPoint> locationPoints) {
        this.locationPoints = Ordering.natural().sortedCopy(locationPoints);
    }

    public LocationPoint getStart() {
        return Iterables.getFirst(locationPoints, null);
    }

    public LocationPoint getEnd() {
        return Iterables.getLast(locationPoints, null);
    }

    public HaversineCalculator.Velocity getVelocity() {
        return new HaversineCalculator().calculateVelocity(Lists.newArrayList(
                Iterables.getFirst(locationPoints, null),
                Iterables.getLast(locationPoints)
        )).get(0);
    }

    public double getMilesTravelled() {
        return DistanceUtils.KM_TO_MILES*(new HaversineCalculator().calculateDistance(locationPoints)/1000);
    }
}
