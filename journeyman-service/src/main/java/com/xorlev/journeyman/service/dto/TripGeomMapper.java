package com.xorlev.journeyman.service.dto;

import com.spatial4j.core.distance.DistanceUtils;
import com.xorlev.journeyman.service.api.TripGeom;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 2013-12-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class TripGeomMapper implements ResultSetMapper<TripGeom> {
    @Override
    public TripGeom map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        TripGeom tripGeom = new TripGeom();
        tripGeom.setTripStart(r.getTimestamp("trip_start"));
        Point start = (Point) ((PGgeometry)r.getObject("start_latlong")).getGeometry();
        Point end = (Point) ((PGgeometry)r.getObject("end_latlong")).getGeometry();

        tripGeom.setStartLatitude(start.getY());
        tripGeom.setEndLatitude(end.getY());
        tripGeom.setStartLongitude(start.getX());
        tripGeom.setEndLongitude(end.getX());
        tripGeom.setSumdist(DistanceUtils.radians2Dist(r.getDouble("sumdist"), DistanceUtils.EARTH_MEAN_RADIUS_KM*1000));

        return tripGeom;
    }
}
