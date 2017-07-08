package com.github.invader.agent.config.client;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationClientFactoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnHttpConfigurationClientForHttpPrefix() {
        //given
        String source = "http://localhost:8080/";

        //when
        ConfigurationClient client = ConfigurationClientFactory.createClient(source, "group", "app");

        //then
        assertThat(client).isInstanceOf(HttpConfigurationClient.class);
    }

    @Test
    public void shouldReturnHttpConfigurationClientForHttpsPrefix() {
        //given
        String source = "https://localhost:8080/";

        //when
        ConfigurationClient client = ConfigurationClientFactory.createClient(source, "group", "app");

        //then
        assertThat(client).isInstanceOf(HttpConfigurationClient.class);
    }

    @Test
    public void shouldReturnFileConfigurationClientForFilePrefix() {
        //given
        String source = "file://config.json";

        //when
        ConfigurationClient client = ConfigurationClientFactory.createClient(source, "group", "app");

        //then
        assertThat(client).isInstanceOf(FileConfigurationClient.class);
    }

    @Test
    public void shouldThrowExceptionIfUnknownProtocol() {
        //given
        String source = "files://config.json";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Unsupported protocol 'files' in source 'files://config.json'");

        //when
        ConfigurationClient client = ConfigurationClientFactory.createClient(source, "group", "app");

        //then exception

    }


    @Test
    public void shouldThrowExceptionForMalformedSource() {
        //given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Configuration source 'file:/wrongSeparator' must match ^([a-z]+)://(.*)$");

        //when
        ConfigurationClient client = ConfigurationClientFactory.createClient("file:/wrongSeparator", "group", "app");

        //then exception
    }




}