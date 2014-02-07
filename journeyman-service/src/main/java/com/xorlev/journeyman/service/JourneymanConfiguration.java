package com.xorlev.journeyman.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

public class JourneymanConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration() {
        if (System.getenv("DATABASE_URL") != null) {
            try {
                URI dbUri = new URI(System.getenv("DATABASE_URL"));

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                database.setUser(username);
                database.setPassword(password);
                database.setUrl(dbUrl);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }

        return database;
    }
}