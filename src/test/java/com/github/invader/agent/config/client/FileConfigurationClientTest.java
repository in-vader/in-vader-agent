package com.github.invader.agent.config.client;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Jacek on 2017-06-30.
 */
public class FileConfigurationClientTest {

    @Test
    public void shouldLoadFileConfig() {
        //given
        FileConfigurationClient client = new FileConfigurationClient("src/test/resources/config.json");

        //when
        Map<String, Map> configuration = client.getConfiguration("group", "app");

        //then
        assertThat(configuration).containsOnlyKeys("delay", "failure");
        assertThat(configuration.get("delay"))
                .containsEntry("min", 120)
                .containsEntry("max", 240);
        assertThat(configuration.get("failure"))
                .containsEntry("probability", 0.6);
    }

}