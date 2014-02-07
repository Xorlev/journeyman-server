package com.xorlev.journeyman.service.api;

import lombok.Data;

import javax.ws.rs.Path;
import java.util.Date;
import java.util.Map;

/**
 * 2013-11-09
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
@Path("/location/{eid}")
public class LocationPoint implements Comparable<LocationPoint> {
    String eid;
    double longitude;
    double latitude;
    double accuracy;
    Date timestamp;
    Date tripStart;
    Map<Integer, double[]> sensorReadings;
    Map<String, Object> metadata;

    @Override
    public int compareTo(LocationPoint o) {
        return timestamp.compareTo(o.timestamp);
    }

//    double relative_humidity;
//    double relative_humidity_accuracy;
//    double ambient_temperature;
//    double ambient_temperature_accuracy;
//    double pressure;
//    double pressure_accuracy;
}
