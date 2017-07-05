package com.github.invader.agent.config.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationClientFactory {

    private static final Pattern URL_MATCH_PATTERN = Pattern.compile("^([a-z]+)://(.*)$");

    public static ConfigurationClient createClient(String source, String group, String appName) {
        Matcher matcher = URL_MATCH_PATTERN.matcher(source);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Configuration source '"+source+"' must match "+URL_MATCH_PATTERN.pattern());
        }
        String protocol = matcher.group(1);
        String remainder = matcher.group(2);

        switch (protocol) {
            case "http":
            case "https": return new HttpConfigurationClient(source, group, appName);
            case "file": return new FileConfigurationClient(remainder);
            default: throw new IllegalArgumentException("Unsupported protocol '"+protocol+"' in source '"+source+"'");
        }
    }
}
