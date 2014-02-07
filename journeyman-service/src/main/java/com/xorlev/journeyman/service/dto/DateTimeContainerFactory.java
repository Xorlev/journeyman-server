package com.xorlev.journeyman.service.dto;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

import java.util.Date;

/**
 * 2013-11-15
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class DateTimeContainerFactory implements ContainerFactory<DateTime> {
    @Override
    public boolean accepts(Class<?> type) {
        return type.isAssignableFrom(Date.class);
    }

    @Override
    public ContainerBuilder<DateTime> newContainerBuilderFor(Class<?> type) {
        return new DateTimeContainerBuilder();
    }

    private static class DateTimeContainerBuilder implements ContainerBuilder<DateTime> {
        Date date;

        @Override
        public ContainerBuilder<DateTime> add(Object it) {
            this.date = (Date)it;
            return this;
        }

        @Override
        public DateTime build() {
            return new DateTime(date);
        }
    }
}
