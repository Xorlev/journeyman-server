package com.xorlev.journeyman.service.api;

import lombok.Data;
import org.postgis.Point;

import java.util.Date;

/**
 * 2013-12-17
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
public class TripGeom {
    Double startLatitude;
    Double endLatitude;
    Double startLongitude;
    Double endLongitude;
    Date tripStart;
    double sumdist;

    public TripGeom() {}
}
