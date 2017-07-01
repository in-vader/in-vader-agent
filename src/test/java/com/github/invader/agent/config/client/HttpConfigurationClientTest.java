package com.github.invader.agent.config.client;

import com.github.invader.agent.config.client.ConfigurationClient;
import com.github.invader.agent.config.client.HttpConfigurationClient;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpConfigurationClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private ConfigurationClient configurationClient;

    @Before
    public void setUp() {
        configurationClient = new HttpConfigurationClient("http://localhost:" + wireMockRule.port());
    }

    @Test
    public void shouldRetrieveConfiguration() {
        // given
        String group = "testGroup";
        String app = "testApp";
        String config =
                "{" +
                        "\"delay\":{" +
                            "\"min\":100," +
                            "\"max\":200" +
                        "}," +
                        "\"failure\":{" +
                            "\"probability\":0.1" +
                        "}" +
                "}";

        stubFor(get(urlEqualTo("/api/groups/testGroup/apps/testApp/agent-config"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withBody(config)
                                .withHeader("content-type", "application/json")));

        // when
        Map<String, Map> configuration = configurationClient.getConfiguration(group, app);

        // then
        assertThat(configuration).containsOnlyKeys("delay", "failure");
        assertThat(configuration.get("delay"))
                .containsEntry("min", 100)
                .containsEntry("max", 200);
        assertThat(configuration.get("failure"))
                .containsEntry("probability", 0.1);
    }
}