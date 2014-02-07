package com.xorlev.journeyman.service.resource;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.xorlev.journeyman.service.api.DeviceKey;
import com.xorlev.journeyman.service.api.LocationPoint;
import com.xorlev.journeyman.service.core.DateParam;
import com.xorlev.journeyman.service.core.HaversineCalculator;
import com.xorlev.journeyman.service.dto.LocationDAO;
import com.yammer.dropwizard.auth.Auth;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

/**
 * 2013-11-09
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/locations")
public class LocationResource {
    final LocationDAO locationDAO;

    public LocationResource(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
    }

    @GET
    public ImmutableList<LocationPoint> getUpdates(@Auth DeviceKey deviceKey, @QueryParam("tripStart") DateParam tripStart) {
        if (tripStart != null) {
            System.out.println(tripStart);
            System.out.println(tripStart.getValue());
            System.out.println(tripStart.getValue().toDate());
            return locationDAO.byTrip(tripStart.getValue().toDate());
        } else {
            return locationDAO.all();
        }
    }

    @GET
    @Path("/velocities")
    public Velocities getUpdatesWithVelocity(@Auth DeviceKey deviceKey, @QueryParam("tripStart") DateParam tripStart) {
        if (tripStart != null) {
            System.out.println(tripStart);
            System.out.println(tripStart.getValue());
            System.out.println(tripStart.getValue().toDate());
            List<LocationPoint> updates = locationDAO.byTrip(tripStart.getValue().toDate());
            List<LocationPoint> ends = Lists.newArrayList(
                    Iterables.getFirst(updates, null));

            for (int i = 1; i < updates.size(); i += updates.size()/20) {
                ends.add(updates.get(i));
            }

            ends.add(Iterables.getLast(updates, null));

            HaversineCalculator calculator = new HaversineCalculator();
//            for (List<LocationUpdate> pair : Lists.partition(updates, 10)) {
//                calculator.calculateVelocity()
//            }

            return new Velocities(calculator.calculateVelocity(ends));
        } else {
//            return locationDAO.all();
        }

        return null;
    }

    public class Velocities {
        List<HaversineCalculator.Velocity> velocities;

        public Velocities(List<HaversineCalculator.Velocity> velocities) {
            this.velocities = velocities;
        }

        public List<HaversineCalculator.Velocity> getVelocities() {
            return velocities;
        }


    }

    @GET
    @Path("/{updateId}")
    public Optional<LocationPoint> getUpdate(@Auth DeviceKey deviceKey, @PathParam("updateId") String updateId) {
        return Optional.fromNullable(locationDAO.byId(updateId));
    }

    @POST
    public Response receiveUpdates(@Auth DeviceKey deviceKey, List<LocationPoint> locationPoints) {
        List<URI> createdUris = Lists.newArrayListWithExpectedSize(locationPoints.size());
        for (LocationPoint locationPoint : locationPoints) {
            System.out.println(locationPoint);
            locationDAO.insertLocation(locationPoint);
            createdUris.add(
                    UriBuilder.fromResource(LocationPoint.class).build(locationPoint)
            );
        }


        return Response.status(200).entity(createdUris).build();
    }
}
