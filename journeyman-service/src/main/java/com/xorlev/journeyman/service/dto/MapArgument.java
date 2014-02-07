package com.xorlev.journeyman.service.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class MapArgument implements ArgumentFactory<Map<String,Object>>
{
    private final ObjectMapper objectMapper;

    public MapArgument(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        return value != null && Map.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final Map<String,Object> value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                try {
                    statement.setString(position, objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    throw new SQLException("Failed to map object", e);
                }
            }
        };
    }
}