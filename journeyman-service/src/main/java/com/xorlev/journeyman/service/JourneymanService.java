package com.xorlev.journeyman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.xorlev.journeyman.service.api.DeviceKey;
import com.xorlev.journeyman.service.core.JDBITripStore;
import com.xorlev.journeyman.service.dto.*;
import com.xorlev.journeyman.service.resource.LocationResource;
import com.xorlev.journeyman.service.resource.TripResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import org.skife.jdbi.v2.DBI;

/**
 * 2013-11-09
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class JourneymanService extends Service<JourneymanConfiguration> {

    @Override
    public void initialize(Bootstrap<JourneymanConfiguration> configurationBootstrap) {
        configurationBootstrap.getObjectMapperFactory().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void run(JourneymanConfiguration configuration, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory();

        final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "postgresql");
        jdbi.registerArgumentFactory(new DateAsTimestampArgument());
        jdbi.registerArgumentFactory(new DateTimeAsTimestampArgument());
        jdbi.registerArgumentFactory(new MapArgument(new ObjectMapper()));
        jdbi.registerContainerFactory(new DateTimeContainerFactory());
        jdbi.registerMapper(new PointMapper());
        jdbi.registerMapper(new PGgeometryMapper());
        final LocationDAO locationDAO = jdbi.onDemand(LocationDAO.class);
        final TripDAO tripDAO = jdbi.onDemand(TripDAO.class);
        final TripStore tripStore = new JDBITripStore(tripDAO, locationDAO);

        environment.addProvider(new BasicAuthProvider<DeviceKey>(new SimpleAuthenticator("5c67be9b7fad7036746f59d226bab8a0"), "API"));

        environment.addFilter(CORSFilter.class, "/*");
        environment.addResource(new TripResource(tripStore));
        environment.addResource(new LocationResource(locationDAO));
    }

    public static void main(String[] args) throws Exception {
        new JourneymanService().run(args);
    }
}
