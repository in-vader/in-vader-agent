package com.github.invader.agent.config.client;


import java.util.Map;

public interface ConfigurationClient {

    Map<String, Map> getConfiguration(String group, String app);

}