package com.github.invader.agent.config;

import com.google.common.base.Preconditions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AgentConfigurationParser {
    static final String CONFIG_FILE_PATH_PROPERTY = "agentConfigFile";

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
        Preconditions.checkArgument(agentConfiguration.getServer() != null, "Server parameter doesn't exist.");
        Preconditions.checkArgument(!agentConfiguration.getServer().isEmpty(), "Server parameter can't be empty.");
        Preconditions.checkArgument(agentConfiguration.getGroup() != null, "Group parameter doesn't exist.");
        Preconditions.checkArgument(!agentConfiguration.getGroup().isEmpty(), "Group parameter can't be empty.");
        Preconditions.checkArgument(agentConfiguration.getName() != null, "Name parameter doesn't exist.");
        Preconditions.checkArgument(!agentConfiguration.getName().isEmpty(), "Name parameter can't be empty.");
    }
}