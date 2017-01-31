package com.github.invader.agent.config;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(agentConfiguration.getServer()).isEqualTo(server);
        assertThat(agentConfiguration.getGroup()).isEqualTo(group);
        assertThat(agentConfiguration.getName()).isEqualTo(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNotProperlyPreparedFile() throws Exception {
        // given
        System.setProperty(AgentConfigurationParser.CONFIG_FILE_PATH_PROPERTY, "src/test/resources/config-empty.yml");

        // when
        agentConfigurationParser.parse();

        // then
        fail("An IllegalArgumentException should have be thrown.");
    }
}