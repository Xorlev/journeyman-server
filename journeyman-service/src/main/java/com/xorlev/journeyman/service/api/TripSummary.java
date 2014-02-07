package com.xorlev.journeyman.service.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;
import java.util.Map;

/**
 * 2013-12-04
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
public class TripSummary {
//    StatisticalSummary statisticalSummary;
//    Map<Double, Double> speedDistribution;
    Trip trip;
    List<TripSegment> segments;

    public TripSummary(Trip trip, List<TripSegment> segments) {
        this.trip = trip;
        this.segments = segments;
    }

    public StatisticalSummary getStatisticalSummary() {
        SummaryStatistics summaryStatistics = new SummaryStatistics();

        for (TripSegment tripSegment : segments) {
            summaryStatistics.addValue(tripSegment.getVelocity().getMilesPerHour());
        }

        return summaryStatistics.getSummary();
    }

    public Map<Double, Double> getSpeedDistribution() {
        DescriptiveStatistics summaryStatistics = new DescriptiveStatistics();

        for (TripSegment tripSegment : segments) {
            summaryStatistics.addValue(tripSegment.getVelocity().getMilesPerHour());
        }

        Map<Double, Double> percentiles = Maps.newTreeMap();
        for (Double percentile : Lists.newArrayList(10d, 25d, 50d, 75d, 90d, 95d, 98d, 99d, 99.5d)) {
            percentiles.put(percentile, summaryStatistics.getPercentile(percentile));
        }

        return percentiles;
    }
}
