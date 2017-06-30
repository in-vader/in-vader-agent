package com.github.invader.agent.config.client;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * Created by Jacek on 2017-06-30.
 */
public class FileConfigurationClient implements ConfigurationClient {
    private final String source;

    public FileConfigurationClient(String source) {
        this.source = source;
    }

    @Override
    public Map<String, Map> getConfiguration(String group, String app) {
        throw new NotImplementedException();
    }
}
