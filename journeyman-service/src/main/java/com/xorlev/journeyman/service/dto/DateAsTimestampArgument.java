package com.xorlev.journeyman.service.dto;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DateAsTimestampArgument implements ArgumentFactory<Date>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        return value != null && java.util.Date.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final Date value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                statement.setTimestamp(position, new java.sql.Timestamp(value.getTime()));
            }
        };
    }
}