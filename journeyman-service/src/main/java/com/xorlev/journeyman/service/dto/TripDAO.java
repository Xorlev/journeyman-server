package com.xorlev.journeyman.service.dto;

import com.google.common.collect.ImmutableList;
import com.xorlev.journeyman.service.api.Trip;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

import java.util.Date;

/**
 * 2013-11-15
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@RegisterMapperFactory(BeanMapperFactory.class)
public interface TripDAO {
    @SqlQuery("SELECT trip_start AS start, MAX(timestamp) AS end FROM location_updates WHERE trip_start IS NOT NULL GROUP BY trip_start ORDER BY trip_start DESC")
    public ImmutableList<Trip> trips();

    @SqlQuery("SELECT trip_start AS start, MAX(timestamp) AS end FROM location_updates WHERE trip_start = :start GROUP BY trip_start ORDER BY trip_start DESC")
    public Trip trip(@Bind("start") Date tripId);
}
