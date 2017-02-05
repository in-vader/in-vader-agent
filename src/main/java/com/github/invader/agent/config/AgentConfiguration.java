package com.github.invader.agent.config;

import lombok.Data;

@Data
public class AgentConfiguration {
    private String server;
    private String group;
    private String name;
    private Log log = new Log();

    @Data
    public static class Log {
        private String fileName = "in-vader-agent.log";
        private String filePath = ".";
        private boolean daily = true;
        private int fileCount = 1;
        private long sizeLimit = 0;
        private String level = "INFO";
    }
}