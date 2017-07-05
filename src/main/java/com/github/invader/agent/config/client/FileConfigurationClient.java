package com.github.invader.agent.config.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class FileConfigurationClient implements ConfigurationClient {
    private final String filePath;

    public FileConfigurationClient(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Map<String, Map> getConfiguration() {
        File file = new File(filePath);
        log.info("Reading config file {}", file.getAbsolutePath());
        try {
            return new ObjectMapper().readValue(file, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
