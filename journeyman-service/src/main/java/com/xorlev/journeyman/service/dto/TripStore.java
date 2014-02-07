package com.xorlev.journeyman.service.dto;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.xorlev.journeyman.service.api.*;
import org.postgis.Point;

import java.util.Date;
import java.util.List;

/**
 * 2013-12-03
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface TripStore {
    public ImmutableList<Trip> tripList();
    public Optional<Trip> getTrip(String tripId);
    public Optional<TripSummary> getTripSummary(String tripId);
    public void insertTripUpdates(Trip trip, List<LocationPoint> updateList);
    public ImmutableList<ProximityResult> similarByEndpoints(String tripId, long meters);
    public ImmutableList<TripGeom> similarTrips(String tripId);

    ImmutableList<Trip> enrichedSimilarTrips(String tripId);

    Optional<Trip> getTrip(Date tripId);
}
