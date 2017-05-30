package com.github.invader.agent.interceptors;

import java.util.Map;

public abstract class Interceptor {

    private boolean enabled;

    public abstract String getName();

    public void setConfig(Map<String, Object> config) {
        if (config == null) {
            setEnabled(false);
        } else {
            applyConfig(config);
            setEnabled(true);
        }
    }

    protected abstract void applyConfig(Map<String, Object> config);

    public boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
