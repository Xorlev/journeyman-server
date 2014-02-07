package com.xorlev.journeyman.service.dto;

/**
 * 2013-11-11
 *
 * @author Michael Rose <michael@fullcontact.com>
 */


import org.postgis.PGgeometry;
import org.skife.jdbi.v2.util.TypedMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Convenience ResultSetMapper for extracting a single value result
 * from a query.
 */
public class PGgeometryMapper extends TypedMapper<PGgeometry>
{
    /**
     * An instance which extracts value from the first field
     */
    public static final PGgeometryMapper FIRST = new PGgeometryMapper(1);


    /**
     * Create a new instance which extracts the value from the first column
     */
    public PGgeometryMapper()
    {
        super();
    }

    /**
     * Create a new instance which extracts the value positionally
     * in the result set
     *
     * @param index 1 based column index into the result set
     */
    public PGgeometryMapper(int index)
    {
        super(index);
    }

    /**
     * Create a new instance which extracts the value by name or alias from the result set
     *
     * @param name The name or alias for the field
     */
    public PGgeometryMapper(String name)
    {
        super(name);
    }

    @Override
    protected PGgeometry extractByName(ResultSet r, String name) throws SQLException
    {
        return (PGgeometry)r.getObject(name);
    }

    @Override
    protected PGgeometry extractByIndex(ResultSet r, int index) throws SQLException
    {
        return (PGgeometry)r.getObject(index);
    }
}
