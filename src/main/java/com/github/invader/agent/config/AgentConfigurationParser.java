package com.github.invader.agent.config;

import org.apache.commons.lang3.Validate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AgentConfigurationParser {

    static final String CONFIG_FILE_PATH_PROPERTY = "invader.config.file";
    private final Yaml parser;

    public AgentConfigurationParser() {
        parser = new Yaml(new Constructor(AgentConfiguration.class));
    }

    public AgentConfiguration parse() {
        Object parseResult;

        try {
            parseResult = parser.load(Files.newInputStream(getConfigFilePath()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        final AgentConfiguration agentConfiguration = (AgentConfiguration) parseResult;

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