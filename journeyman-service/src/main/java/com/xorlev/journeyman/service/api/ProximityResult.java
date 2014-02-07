package com.xorlev.journeyman.service.api;

/**
 * 2013-12-17
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class ProximityResult implements Comparable<ProximityResult> {
    LocationPoint locationPoint;
    Double meters;

    public ProximityResult() {
    }

    public ProximityResult(LocationPoint locationPoint, double meters) {
        this.locationPoint = locationPoint;
        this.meters = meters;
    }

    public LocationPoint getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(LocationPoint locationPoint) {
        this.locationPoint = locationPoint;
    }

    public double getMeters() {
        return meters;
    }

    public void setMeters(double meters) {
        this.meters = meters;
    }

    @Override
    public int compareTo(ProximityResult o) {
        return meters.compareTo(o.meters);
    }
}
