package com.github.invader.agent.config;

import com.github.invader.agent.interceptors.constraints.NonEmptyString;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AgentConfiguration {
    @NotNull @Valid private Config config;
    @NonEmptyString private String group;
    @NonEmptyString private String name;
    private Log log = new Log();
    private Bindings bindings = new Bindings();

    @Data
    public static class Config {
        @NonEmptyString private String source;
        @NotNull @Min(1L) private long intervalSeconds = 10;
    }

    @Data
    public static class Log {
        private String fileName = "in-vader-agent.log";
        private String filePath = ".";
        private boolean daily = true;
        private int fileCount = 1;
        private long sizeLimit = 0;
        private String level = "INFO";
    }

    @Data
    public static class Bindings {
        private String dir;
    }
}