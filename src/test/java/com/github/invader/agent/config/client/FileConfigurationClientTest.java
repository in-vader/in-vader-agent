package com.github.invader.agent.config.client;

import com.github.invader.agent.config.AgentConfigurationLoader;
import com.github.invader.agent.interceptors.PeakInterceptor;
import org.junit.Test;

import java.util.Arrays;
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
        Map<String, Map> configuration = client.getConfiguration();

        //then
        assertThat(configuration).containsOnlyKeys("delay", "failure", "peak");
        assertThat(configuration.get("delay"))
                .containsEntry("min", 120)
                .containsEntry("max", 240);
        assertThat(configuration.get("failure"))
                .containsEntry("probability", 0.6);
        assertThat(configuration.get("peak"))
                .containsEntry("startTime", "19:31:00+02:00")
                .containsEntry("endTime", "19:35:00+02:00")
                .containsEntry("delayMidpoints",
                        Arrays.asList(50,100,150,300,2000,3000,3000,1500,500,300,100,50));
    }

}