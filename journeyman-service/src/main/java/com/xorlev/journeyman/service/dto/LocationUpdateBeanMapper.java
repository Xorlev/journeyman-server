package com.xorlev.journeyman.service.dto;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.xorlev.journeyman.service.api.LocationPoint;
import org.skife.jdbi.v2.BeanMapper;
import org.skife.jdbi.v2.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 2013-11-10
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class LocationUpdateBeanMapper extends BeanMapper<LocationPoint> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    JavaType typeReference = TypeFactory.defaultInstance().constructMapType(
            Map.class,
            Integer.class,
            double[].class);

    public LocationUpdateBeanMapper() {
        super(LocationPoint.class);
    }

    @Override
    public LocationPoint map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
        LocationPoint update = super.map(row, rs, ctx);

//        if (rs.findColumn("sensor_data"))
        update.setTripStart(rs.getTimestamp("trip_start"));

        String sensorJson = rs.getString("sensor_data");
        if (sensorJson != null && !sensorJson.isEmpty()) {

            try {
                update.setSensorReadings((Map<Integer,double[]>)objectMapper.readValue(sensorJson, typeReference));
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize sensor readings", e);
            }
        }

        return update;
    }
}
