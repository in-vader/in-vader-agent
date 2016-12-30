package com.github.invader.agent.config;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AgentConfigurationParserTest {
    private AgentConfigurationParser agentConfigurationParser;

    @Before
    public void setUp() throws Exception {
        agentConfigurationParser = new AgentConfigurationParser();
    }

    @Test
    public void shouldAllValuesBeProvided() throws Exception {
        // given
        System.setProperty(AgentConfigurationParser.CONFIG_FILE_PATH_PROPERTY, "src/test/resources/config-all.yml");

        final String server = "http://localhost:8080";
        final String group = "testGroup";
        final String name = "testAppName";

        // when
        AgentConfiguration agentConfiguration = agentConfigurationParser.parse();

        // then
        assertEquals(server, agentConfiguration.getServer());
        assertEquals(group, agentConfiguration.getGroup());
        assertEquals(name, agentConfiguration.getName());
    }

    @Test
    public void shouldThrowExceptionForNotProperlyPreparedFile() throws Exception {
        // given
        System.setProperty(AgentConfigurationParser.CONFIG_FILE_PATH_PROPERTY, "src/test/resources/config-empty.yml");

        // when
        try {
            agentConfigurationParser.parse();
        } catch (IllegalArgumentException ex) {
            return;
        }

        fail("An IllegalArgumentException should have be thrown.");
    }
}