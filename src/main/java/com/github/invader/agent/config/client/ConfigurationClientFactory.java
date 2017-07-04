package com.github.invader.agent.config.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jacek on 2017-06-30.
 */
public class ConfigurationClientFactory {

    private static final Pattern URL_MATCH_PATTERN = Pattern.compile("^([a-z]+)://(.*)$");

    public static ConfigurationClient createClient(String source, String group, String appName) {
        Matcher matcher = URL_MATCH_PATTERN.matcher(source);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Configuration source '"+source+"' must match "+URL_MATCH_PATTERN.pattern());
        }
        String protocol = matcher.group(1);
        String remainder = matcher.group(2);

        ConfigurationClient client = null;
        switch (protocol) {
            case "http":
            case "https": client = new HttpConfigurationClient(source, group, appName);
                         break;
            case "file": client = new FileConfigurationClient(remainder);
                         break;
            default: throw new IllegalArgumentException("Unsupported protocol '"+protocol+"' in source '"+source+"'");
        }

        return client;
    }
}
