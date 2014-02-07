package com.xorlev.journeyman.service.dto;

/**
 * 2013-11-11
 *
 * @author Michael Rose <michael@fullcontact.com>
 */


import org.postgis.PGgeometry;
import org.postgis.Point;
import org.skife.jdbi.v2.util.TypedMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Convenience ResultSetMapper for extracting a single value result
 * from a query.
 */
public class PointMapper extends TypedMapper<Point>
{
    /**
     * An instance which extracts value from the first field
     */
    public static final PointMapper FIRST = new PointMapper(1);


    /**
     * Create a new instance which extracts the value from the first column
     */
    public PointMapper()
    {
        super();
    }

    /**
     * Create a new instance which extracts the value positionally
     * in the result set
     *
     * @param index 1 based column index into the result set
     */
    public PointMapper(int index)
    {
        super(index);
    }

    /**
     * Create a new instance which extracts the value by name or alias from the result set
     *
     * @param name The name or alias for the field
     */
    public PointMapper(String name)
    {
        super(name);
    }

    @Override
    protected Point extractByName(ResultSet r, String name) throws SQLException
    {
        return (Point) ((PGgeometry)r.getObject(name)).getGeometry();
    }

    @Override
    protected Point extractByIndex(ResultSet r, int index) throws SQLException
    {
        return (Point) ((PGgeometry)r.getObject(index)).getGeometry();
    }
}
