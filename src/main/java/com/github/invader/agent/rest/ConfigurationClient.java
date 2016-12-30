package com.github.invader.agent.rest;

import com.github.invader.controller.transport.Config;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Optional;

public class ConfigurationClient {
    private final OkHttpClient client;
    private final String serverUrl;

    public ConfigurationClient(String serverUrl) {
        this.client = new OkHttpClient();
        this.serverUrl = serverUrl;
    }

    public Optional<Config> getConfiguration(String group, String name) {
        try {
            Request request = new Request.Builder()
                    .url(String.format(serverUrl + "/api/groups/%s/apps/%s/config", group, name))
                    .build();

            Response response = client.newCall(request).execute();

            return Optional.of(decodeConfigFromJSON(response.body().string()));
        } catch(IOException e) {
            e.printStackTrace();

            return Optional.empty();
        }
    }

    private Config decodeConfigFromJSON(String json) {
        return new Gson().fromJson(json, Config.class);
    }
}