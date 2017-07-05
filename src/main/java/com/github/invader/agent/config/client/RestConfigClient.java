package com.github.invader.agent.config.client;

import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface RestConfigClient {

    @RequestLine("GET /api/groups/{group}/apps/{app}/agent-config")
    Map<String, Map> getConfiguration(@Param("group") String group, @Param("app") String app);

}
