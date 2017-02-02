package com.github.invader.agent.rest;


import feign.*;
import feign.jackson.JacksonDecoder;

import java.util.Map;

public interface ConfigurationClient {

    int CONNECT_TIMEOUT_MILLIS = 1 * 1000;
    int READ_TIMEOUT_MILLIS = 2 * 1000;

    @RequestLine("GET /api/groups/{group}/apps/{app}/agent-config")
    Map<String, Map> getConfiguration(@Param("group") String group, @Param("app") String app);

    static ConfigurationClient connect(String serverUrl) {
        return
                Feign.builder()
                        .decoder(new JacksonDecoder())
                        .retryer(new Retryer.Default())
                        .options(new Request.Options(CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS))
                        .target(ConfigurationClient.class, serverUrl);
    }
}