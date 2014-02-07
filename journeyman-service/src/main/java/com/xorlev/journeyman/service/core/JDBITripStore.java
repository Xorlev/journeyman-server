package com.xorlev.journeyman.service.core;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.spatial4j.core.distance.DistanceUtils;
import com.xorlev.journeyman.service.api.*;
import com.xorlev.journeyman.service.dto.LocationDAO;
import com.xorlev.journeyman.service.dto.TripDAO;
import com.xorlev.journeyman.service.dto.TripStore;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * 2013-12-04
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class JDBITripStore implements TripStore {
    private final TripDAO tripDAO;
    private final LocationDAO locationDAO;

    public JDBITripStore(TripDAO tripDAO, LocationDAO locationDAO) {
        this.tripDAO = tripDAO;
        this.locationDAO = locationDAO;
    }

    @Override
    public ImmutableList<Trip> tripList() {
        return tripDAO.trips();
    }

    @Override
    public Optional<Trip> getTrip(String tripId) {
        return Optional.absent();
//        return Optional.fromNullable(tripDAO.trip(tripId));
    }

    @Override
    public Optional<Trip> getTrip(Date tripId) {
        return Optional.fromNullable(tripDAO.trip(tripId));
    }

    @Override
    public Optional<TripSummary> getTripSummary(String tripId) {
        DateParam dateParam = new DateParam(tripId);
        ImmutableList<LocationPoint> locationPoints = locationDAO.byTrip(dateParam.getValue().toDate());
        Trip trip = new Trip();
        trip.setStart(dateParam.getValue().toDate());
        trip.setEnd(Iterables.getFirst(locationPoints, null).getTimestamp());

        return Optional.of(new TripSummary(trip, segmentizeUpdates(locationPoints)));
    }

    protected List<TripSegment> segmentizeUpdates(List<LocationPoint> locationPoints) {
        return Lists.transform(Lists.partition(locationPoints, 25), new Function<List<LocationPoint>, TripSegment>() {
            @Override
            public TripSegment apply(List<LocationPoint> input) {
                return new TripSegment(input);
            }
        });
    }

    @Override
    public void insertTripUpdates(Trip trip, List<LocationPoint> updateList) {
    }

    @Override
    public ImmutableList<ProximityResult> similarByEndpoints(String tripId, long meters) {
        DateParam dateParam = new DateParam(tripId);
        double radians = DistanceUtils.dist2Radians(meters, DistanceUtils.EARTH_MEAN_RADIUS_KM * 1000);

        final List<LocationPoint> byTrip = locationDAO.byTrip(dateParam.getValue().toDate());

        if (byTrip.size() == 0) return ImmutableList.of();

        return findSimilarPoints(dateParam, radians, byTrip);
    }

    @Override
    public ImmutableList<TripGeom> similarTrips(String tripId) {
        DateParam dateParam = new DateParam(tripId);

        final List<LocationPoint> byTrip = locationDAO.byTrip(dateParam.getValue().toDate());
        LocationPoint last = Iterables.getFirst(byTrip, null); // reverse sort
        LocationPoint first = Iterables.getLast(byTrip, null);

        if (first != null && last != null) {
            System.out.println(first);
            System.out.println(last);
            ImmutableList<TripGeom> similarTrips = locationDAO.similarTrips(
                    first.getLatitude(), first.getLongitude(), last.getLatitude(), last.getLongitude());

            int thresholdIndex = similarTrips.size();
            if (similarTrips.size() > 1) {
                double previousIncrease = 0;
                for (int i = 1; i < similarTrips.size(); i++) {
                    double increase = similarTrips.get(i).getSumdist()-similarTrips.get(i-1).getSumdist();

                    System.out.println(increase);

                    if (increase > 50) {
                        thresholdIndex = i;
                        break;
                    }
                }
            }

            return ImmutableList.copyOf(Iterables.skip(Iterables.limit(similarTrips, thresholdIndex),1));
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public ImmutableList<Trip> enrichedSimilarTrips(String tripId) {
        DateParam dateParam = new DateParam(tripId);

        final List<LocationPoint> byTrip = locationDAO.byTrip(dateParam.getValue().toDate());
        LocationPoint last = Iterables.getFirst(byTrip, null); // reverse sort
        LocationPoint first = Iterables.getLast(byTrip, null);

        if (first != null && last != null) {
            System.out.println(first);
            System.out.println(last);
            ImmutableList<TripGeom> similarTrips = locationDAO.similarTrips(
                    first.getLatitude(), first.getLongitude(), last.getLatitude(), last.getLongitude());

            int thresholdIndex = similarTrips.size();
            if (similarTrips.size() > 1) {
                double previousIncrease = 0;
                for (int i = 1; i < similarTrips.size(); i++) {
                    double increase = similarTrips.get(i).getSumdist()-similarTrips.get(i-1).getSumdist();

                    System.out.println(increase);

                    if (increase > 50) {
                        thresholdIndex = i;
                        break;
                    }
                }
            }

            return ImmutableList.copyOf(Iterables.transform(Iterables.skip(Iterables.limit(similarTrips, thresholdIndex),1), new Function<TripGeom, Trip>() {
                @Nullable
                @Override
                public Trip apply(TripGeom input) {
                    return getTrip(input.getTripStart()).orNull();
                }
            }));
        } else {
            return ImmutableList.of();
        }
    }

    private ImmutableList<ProximityResult> findSimilarPoints(DateParam dateParam, double radians, final List<LocationPoint> byTrip) {
        List<LocationPoint> nearby = locationDAO.locationsInRadiusOfDestination(dateParam.getValue().toDate(), radians);

        return Ordering.natural().immutableSortedCopy(
                Lists.transform(
                        nearby, new Function<LocationPoint, ProximityResult>() {
                    @Override
                    public ProximityResult apply(LocationPoint input) {
                        LocationPoint origin = byTrip.get(0);
                        System.out.println(origin);

                        double meters = DistanceUtils.radians2Dist(
                                DistanceUtils.distHaversineRAD(
                                        origin.getLatitude(), origin.getLongitude(), input.getLatitude(),
                                        input.getLongitude()), DistanceUtils.EARTH_MEAN_RADIUS_KM * 1000);

                        return new ProximityResult(input, meters);
                    }
                }));
    }
}
