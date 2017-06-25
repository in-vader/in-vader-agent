package com.github.invader.agent.interceptors.binding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.invader.agent.config.AgentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class InterceptorBindingLoader {

    private final AgentConfiguration agentConfiguration;
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public InterceptorBindingLoader(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    public List<InterceptorBinding> loadBindings() {
        List<InterceptorBinding> bindings = new ArrayList<>();

        try (InputStream defaultBindingStream = this.getClass().getResourceAsStream("/default-interceptor-bindings.yml")) {
            List<InterceptorBinding> defaultBindings = loadBindingsFromStream(defaultBindingStream);
            bindings.addAll(defaultBindings);
            log.debug("Loaded default bindings {}", defaultBindings);
        } catch (IOException e) {
            log.error("Failed to load default bindings", e);
            throw new RuntimeException("Failed to load default bindings", e);
        }

        if (StringUtils.isNotBlank(agentConfiguration.getBindings().getDir())) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(agentConfiguration.getBindings().getDir()), "*.yml")) {
                for (Path externalBindingFile : directoryStream) {
                    try (InputStream externalBindingStream = Files.newInputStream(externalBindingFile)) {
                        InterceptorBinding binding = loadBindingFromStream(externalBindingStream);
                        bindings.add(binding);
                        log.debug("Loaded external binding {}", binding);
                    } catch (IOException e) {
                        log.warn("Failed to load external bindings", e);
                    }
                }
            } catch (IOException e) {
                log.warn("Failed to open external bindings folder", e);
            }
        }

        log.debug("Loaded {} bindings", bindings.size());

        return bindings;
    }

    private List<InterceptorBinding> loadBindingsFromStream(InputStream bindingsStream) throws IOException {
        return Arrays.stream(objectMapper.readValue(bindingsStream, InterceptorBinding[].class)).collect(Collectors.toList());
    }

    private InterceptorBinding loadBindingFromStream(InputStream bindingStream) throws IOException {
        return objectMapper.readValue(bindingStream, InterceptorBinding.class);
    }
}
