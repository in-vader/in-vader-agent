package com.github.invader.agent.config.client;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;

import java.util.Map;

/**
 * Created by Jacek on 2017-06-30.
 */
public class HttpConfigurationClient implements ConfigurationClient {

    int CONNECT_TIMEOUT_MILLIS = 1 * 1000;
    int READ_TIMEOUT_MILLIS = 2 * 1000;

    private final RestConfigClient restClient;

    public HttpConfigurationClient(String source) {
        this.restClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .retryer(new Retryer.Default())
                .options(new Request.Options(CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS))
                .target(RestConfigClient.class, source);
    }

    @Override
    public Map<String, Map> getConfiguration(String group, String app) {
        return restClient.getConfiguration(group, app);
    }
}
