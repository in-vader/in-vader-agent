package com.github.invader.agent.config;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AgentConfigurationLoaderTest {
    private AgentConfigurationLoader agentConfigurationLoader;

    @Before
    public void setUp() throws Exception {
        agentConfigurationLoader = new AgentConfigurationLoader();
    }

    @Test
    public void shouldAllValuesBeProvided() throws Exception {
        // given
        final String configFilePath = "src/test/resources/config-all.yml";

        final String server = "http://localhost:8080";
        final String group = "testGroup";
        final String name = "testAppName";

        // when
        AgentConfiguration agentConfiguration = agentConfigurationLoader.load(configFilePath);

        // then
        assertThat(agentConfiguration.getConfig().getSource()).isEqualTo(server);
        assertThat(agentConfiguration.getGroup()).isEqualTo(group);
        assertThat(agentConfiguration.getName()).isEqualTo(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForNotProperlyPreparedFile() throws Exception {
        // given
        final String configFilePath = "src/test/resources/config-empty.yml";

        // when
        agentConfigurationLoader.load(configFilePath);

        // then
        fail("An IllegalArgumentException should have be thrown.");
    }
}