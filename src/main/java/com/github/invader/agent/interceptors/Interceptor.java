package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.constraints.UnparseableValueException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
public abstract class Interceptor {

    private boolean enabled;

    public abstract String getName();

    public void setConfig(Map<String, Object> config) {
        if (config == null) {
            setEnabled(false);
        } else {
            try {
                applyConfig(config);
                setEnabled(true);
            } catch (ConstraintViolationException e) {
                log.error("Interceptor[{}] invalid configuration: '{}'", getName(), e.getMessage());
                e.getConstraintViolations().stream().forEach(c -> log.error(c.getMessage()));
                setEnabled(false);
            } catch (UnparseableValueException e) {
                log.error("Interceptor[{}] unparseable config: '{}'", getName(), e.getMessage());
                setEnabled(false);
            }
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
