package com.xorlev.journeyman.service.core;

import com.google.common.collect.Lists;
import com.spatial4j.core.distance.DistanceUtils;
import com.xorlev.journeyman.service.api.LocationPoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;
import java.util.List;

/**
 * 2013-11-15
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class HaversineCalculator {
    public List<Velocity> calculateVelocity(List<LocationPoint> locationPoints) {
//        List<LocationUpdate> locationUpdates = null;//trip.getLocationUpdates();
        List<Velocity> velocities = Lists.newArrayList();
        for (int i = 1; i < locationPoints.size(); i++) {
            LocationPoint prev = locationPoints.get(i-1);
            LocationPoint current = locationPoints.get(i);

            double distanceRAD = DistanceUtils.distHaversineRAD(
                    DistanceUtils.toRadians(prev.getLatitude()),DistanceUtils.toRadians(prev.getLongitude()),
                    DistanceUtils.toRadians(current.getLatitude()),DistanceUtils.toRadians(current.getLongitude())
            );

            double distanceMeters = DistanceUtils.radians2Dist(distanceRAD, DistanceUtils.EARTH_MEAN_RADIUS_KM)*1000;

            velocities.add(new Velocity(prev.getTimestamp(), current.getTimestamp(), distanceMeters));
        }

        return velocities;
    }

    public double calculateDistance(List<LocationPoint> locationPoints) {
//        List<LocationUpdate> locationUpdates = null;//trip.getLocationUpdates();
        double distanceMeters = 0;
        for (int i = 1; i < locationPoints.size(); i++) {
            LocationPoint prev = locationPoints.get(i-1);
            LocationPoint current = locationPoints.get(i);

            double distanceRAD = DistanceUtils.distHaversineRAD(
                    DistanceUtils.toRadians(prev.getLatitude()),DistanceUtils.toRadians(prev.getLongitude()),
                    DistanceUtils.toRadians(current.getLatitude()),DistanceUtils.toRadians(current.getLongitude())
            );

            distanceMeters += DistanceUtils.radians2Dist(distanceRAD, DistanceUtils.EARTH_MEAN_RADIUS_KM)*1000;
        }

        return distanceMeters;
    }

    public class Velocity {
        DateTime start;
        DateTime end;
        double meters;

        Velocity(Date start, Date end, double meters) {
            this.start = new DateTime(start);
            this.end = new DateTime(end);
            this.meters = meters;
        }

        Velocity(DateTime start, DateTime end, double meters) {
            this.start = start;
            this.end = end;
            this.meters = meters;
        }

        public DateTime getStart() {
            return start;
        }

        public DateTime getEnd() {
            return end;
        }

        public double getMeters() {
            return meters;
        }

        public Duration getDuration() {
            return new Duration(start, end);
        }

        public double getMilesPerHour() {
            double metersPerSecond = meters / getDuration().getStandardSeconds();
            return DistanceUtils.KM_TO_MILES * metersPerSecond / 1000 * 3600;
        }

        public double getMetersPerSecond() {
            return meters / getDuration().getStandardSeconds();
        }


    }
}
