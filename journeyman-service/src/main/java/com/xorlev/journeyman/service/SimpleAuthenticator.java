package com.xorlev.journeyman.service;

import com.google.common.base.Optional;
import com.xorlev.journeyman.service.api.DeviceKey;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

/**
 * 2013-11-17
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public class SimpleAuthenticator implements Authenticator<BasicCredentials, DeviceKey> {
    DeviceKey password;

    public SimpleAuthenticator(String password) {
        this.password = new DeviceKey(password);
    }

    @Override
    public Optional<DeviceKey> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        if (password.getDeviceKey().equals(basicCredentials.getUsername())) {
            return Optional.of(password);
        }

        return Optional.absent();
    }
}
