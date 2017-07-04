package com.github.invader.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.invader.agent.interceptors.validation.JavaxValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

@Slf4j
public class AgentConfigurationLoader {

    static final String CONFIG_FILE_PATH_PROPERTY = "invader.config.file";
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public AgentConfiguration load() {
        return load(System.getProperty(CONFIG_FILE_PATH_PROPERTY));
    }

    public AgentConfiguration load(String configurationFilePath) {
        AgentConfiguration agentConfiguration;

        try {
            agentConfiguration = objectMapper.readValue(Files.newInputStream(Paths.get(configurationFilePath)), AgentConfiguration.class);// parser.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Set<ConstraintViolation<AgentConfiguration>> violations = JavaxValidatorFactory.createValidator().validate(agentConfiguration);
        if (!violations.isEmpty()) {
            log.error("-------- Agent configuration invalid ---------");
            violations.forEach(v -> log.error(v.getPropertyPath()+": "+v.getMessage()));
            throw new ConstraintViolationException("Agent configuration invalid. See error log for details.", violations);
        }

        return agentConfiguration;
    }

    private void checkPreconditions(AgentConfiguration agentConfiguration) {
        //TODO: consider replacing with javax.constraints annotations
        Validate.notBlank(agentConfiguration.getConfig().getSource(), "Source parameter can't be empty.");
        Validate.notBlank(agentConfiguration.getGroup(), "Group parameter can't be empty.");
        Validate.notBlank(agentConfiguration.getName(), "Name parameter can't be empty.");
    }
}