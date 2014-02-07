package com.xorlev.journeyman.service.dto;

/**
 * 2013-11-11
 *
 * @author Michael Rose <michael@fullcontact.com>
 */

import org.joda.time.DateTime;
import org.skife.jdbi.v2.util.TypedMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Convenience ResultSetMapper for extracting a single value result
 * from a query.
 */
public class DateTimeMapper extends TypedMapper<DateTime>
{
    /**
     * An instance which extracts value from the first field
     */
    public static final DateTimeMapper FIRST = new DateTimeMapper(1);


    /**
     * Create a new instance which extracts the value from the first column
     */
    public DateTimeMapper()
    {
        super();
    }

    /**
     * Create a new instance which extracts the value positionally
     * in the result set
     *
     * @param index 1 based column index into the result set
     */
    public DateTimeMapper(int index)
    {
        super(index);
    }

    /**
     * Create a new instance which extracts the value by name or alias from the result set
     *
     * @param name The name or alias for the field
     */
    public DateTimeMapper(String name)
    {
        super(name);
    }

    @Override
    protected DateTime extractByName(ResultSet r, String name) throws SQLException
    {
        Timestamp ts = r.getTimestamp(name); // column is TIMESTAMPTZ
        return  ts !=null ? new DateTime(ts.getTime()) : null;
    }

    @Override
    protected DateTime extractByIndex(ResultSet r, int index) throws SQLException
    {
        Timestamp ts = r.getTimestamp(index); // column is TIMESTAMPTZ
        return  ts !=null ? new DateTime(ts.getTime()) : null;
    }
}
