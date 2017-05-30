package com.github.invader.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AgentConfigurationParser {

    static final String CONFIG_FILE_PATH_PROPERTY = "invader.config.file";
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public AgentConfiguration parse() {
        AgentConfiguration agentConfiguration;

        try {
            agentConfiguration = objectMapper.readValue(Files.newInputStream(getConfigFilePath()), AgentConfiguration.class);// parser.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        checkPreconditions(agentConfiguration);

        return agentConfiguration;
    }

    private Path getConfigFilePath() {
        return Paths.get(System.getProperty(CONFIG_FILE_PATH_PROPERTY));
    }

    private void checkPreconditions(AgentConfiguration agentConfiguration) {
        Validate.notBlank(agentConfiguration.getServer(), "Server parameter can't be empty.");
        Validate.notBlank(agentConfiguration.getGroup(), "Group parameter can't be empty.");
        Validate.notBlank(agentConfiguration.getName(), "Name parameter can't be empty.");
    }
}