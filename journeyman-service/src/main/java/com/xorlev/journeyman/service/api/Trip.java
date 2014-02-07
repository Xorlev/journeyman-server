package com.xorlev.journeyman.service.api;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Date;
import java.util.List;

/**
 * 2013-11-15
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Data
public class Trip {
    Date start;
    Date end;

    public Period getLength() {
        return new Period(new DateTime(start), new DateTime(end));
    }
}
