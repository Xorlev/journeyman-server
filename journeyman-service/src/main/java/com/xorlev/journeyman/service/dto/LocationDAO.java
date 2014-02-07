package com.xorlev.journeyman.service.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import com.xorlev.journeyman.service.api.LocationPoint;
import com.xorlev.journeyman.service.api.Trip;
import com.xorlev.journeyman.service.api.TripGeom;
import org.postgis.Point;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 2013-11-09
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@RegisterMapper(LocationUpdateBeanMapper.class)
public interface LocationDAO {
    @SqlUpdate("INSERT INTO location_updates (eid, latitude, longitude, accuracy, timestamp, trip_start, sensor_data)" +
                       " VALUES (CAST(:eid AS uuid), :latitude, :longitude, :accuracy, :timestamp, :tripStart, CAST(:sensorReadings AS json))")
    public void insertLocation(@BindBean LocationPoint locationPoint);

    @SqlQuery("SELECT * FROM location_updates WHERE trip_start IS NOT NULL ORDER BY timestamp DESC")
    public ImmutableList<LocationPoint> all();

    @SqlQuery("SELECT * FROM location_updates WHERE eid = :eid")
    public LocationPoint byId(@Bind("eid") String eid);

    @SqlQuery("SELECT * FROM location_updates WHERE trip_start=:tripStart ORDER BY timestamp DESC")
    public ImmutableList<LocationPoint> byTrip(@Bind("tripStart") Date tripStart);

    @SqlQuery("SELECT DISTINCT trip_start FROM location_updates WHERE trip_start IS NOT NULL")
    public ImmutableList<Timestamp> trips();

    @SqlQuery("SELECT * FROM location_updates " +
                      "WHERE ST_DWithin(latlong, " +
                      "(SELECT latlong FROM location_updates WHERE trip_start=:tripStart" +
                      " ORDER BY timestamp DESC LIMIT 1), :radians)")
    public ImmutableList<LocationPoint> locationsInRadiusOfDestination(@Bind("tripStart") Date tripStart, @Bind("radians") double radians);

    /**
     * Similar trips
     *
     * SELECT
     start_latlong,end_latlong,trip_start,
     (ST_Distance(start_latlong, ST_SetSRID(ST_MakePoint(-104.99671208, 39.75444141), 4326))+
     ST_Distance(end_latlong, ST_SetSRID(ST_MakePoint(-105.13576746, 39.71971161), 4326))) AS sumdist
     FROM
     trip_start_end_view
     ORDER BY sumdist ASC;
     */
    @SqlQuery("SELECT \n" +
                      "\tstart_latlong,end_latlong,trip_start,\n" +
                      "\tSQRT(" +
                      "(ST_Distance(start_latlong, ST_SetSRID(ST_MakePoint(:startLong, :startLat), 4326))*(ST_Distance(start_latlong, ST_SetSRID(ST_MakePoint(:startLong, :startLat), 4326))+\n" +
                      "\tST_Distance(end_latlong, ST_SetSRID(ST_MakePoint(:endLong, :endLat), 4326)))*ST_Distance(end_latlong, ST_SetSRID(ST_MakePoint(:endLong, :endLat), 4326)))) AS sumdist\n" +
                      "FROM \n" +
                      "\ttrip_start_end_view\n" +
                      "ORDER BY sumdist ASC;")
    @RegisterMapper(TripGeomMapper.class)
    public ImmutableList<TripGeom> similarTrips(@Bind("startLat") Double startLat,
                                                @Bind("startLong") Double startLong,
                                                @Bind("endLat") Double endLat,
                                                @Bind("endLong") Double endLong);

}
