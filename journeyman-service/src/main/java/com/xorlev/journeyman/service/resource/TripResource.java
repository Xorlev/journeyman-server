package com.xorlev.journeyman.service.resource;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xorlev.journeyman.service.api.*;
import com.xorlev.journeyman.service.dto.TripStore;
import com.yammer.dropwizard.auth.Auth;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.postgis.Point;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;

/**
 * 2013-11-09
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class TripResource {
    final TripStore tripStore;

    public TripResource(TripStore tripStore) {
        this.tripStore = tripStore;
    }

    @GET
    @Path("trips")
    public ImmutableList<Trip> getTrips(@Auth DeviceKey deviceKey) {
        return tripStore.tripList();
    }

    @GET
    @Path("trip/{tripId}")
    public Optional<Trip> trip(@PathParam("tripId") String tripId) {
        return tripStore.getTrip(tripId);
    }

    @GET
    @Path("trip/{tripId}/summary")
    public Optional<TripSummary> tripSummary(@PathParam("tripId") String tripId) {
        return tripStore.getTripSummary(tripId);
    }

    @GET
    @Path("trip/{tripId}/similarByEndpoints")
    public ImmutableList<ProximityResult> similarByEndpoints(@Auth DeviceKey deviceKey, @PathParam(
            "tripId") String tripId) {
        return tripStore.similarByEndpoints(tripId, 100);
    }

    @GET
    @Path("trip/{tripId}/similarTrips")
    public ImmutableList<TripGeom> similarTrips(@Auth DeviceKey deviceKey, @PathParam(
            "tripId") String tripId) {

        return tripStore.similarTrips(tripId);
    }

    @GET
    @Path("trip/{tripId}/similarTripsWithSummary")
    public TripHistogram similarTripsWithSummary(@Auth DeviceKey deviceKey, @PathParam(
            "tripId") String tripId) {

        return new TripHistogram(tripStore.enrichedSimilarTrips(tripId));
    }


    @POST
    @Path("trips")
    public Response insertTrip(@Auth DeviceKey deviceKey, @Valid Trip trip) {
        return Response.created(UriBuilder.fromResource(Trip.class).build(trip)).build();
    }

    class TripHistogram {
        List<Trip> trips;

        TripHistogram(List<Trip> trips) {
            this.trips = trips;
        }

        public List<Trip> getTrips() {
            return trips;
        }

        public Map<Double, Period> getDurationHistogram() {
            DescriptiveStatistics summaryStatistics = new DescriptiveStatistics();

            for (Trip trip : trips) {
                summaryStatistics.addValue(trip.getLength().toStandardSeconds().getSeconds());
            }

            Map<Double, Period> percentiles = Maps.newTreeMap();
            for (Double percentile : Lists.newArrayList(10d, 25d, 50d, 75d, 90d, 95d, 98d, 99d, 99.5d)) {
                Period duration = Seconds.seconds((int)summaryStatistics.getPercentile(percentile)).toStandardDuration().toPeriod();
                percentiles.put(percentile, duration);
            }

            return percentiles;
        }
    }
}
