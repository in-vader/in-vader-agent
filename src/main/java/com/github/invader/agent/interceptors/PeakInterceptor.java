package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.constraints.UnparseableValueException;
import com.github.invader.agent.interceptors.validation.JavaxValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static javax.validation.Validation.*;

/**
 * Enables simulation of a short-term peak of delay of a service.
 *
 * "peak": {
 *     "startTime": "15:00:00",
 *     "endTime":   "16:00:00",
 *     "delayMidpoints": [100,500,1000,700,700,300,300,300,100,100,100,100]
 * }
 *
 *         response [ms]
 *          ^
 *   1000ms |   #
 *          |  ###
 *          |  #####
 *    500ms |  #####
 *          |  #####
 *          |  ########
 *    100ms | #############
 *          +-|-----|-----|---------> time [HH:MM]
 *          15:00 15:30 16:00
 */
@Slf4j
public class PeakInterceptor extends Interceptor {

    private PeakProfile peakProfile;
    private Consumer<Long> sleeper;
    private PeakCalculator peakCalculator = new PeakCalculator();
    private Validator validator = JavaxValidatorFactory.createValidator();

    public PeakInterceptor(Consumer<Long> sleeper) {
        this.sleeper = sleeper;
        this.peakProfile = PeakProfile
                .builder()
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.NOON)
                .delayMidpoints(Arrays.asList(0))
                .build();
    }

    public PeakInterceptor() {
        this(delay -> {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public String getName() {
        return "peak";
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable) throws Exception {
        if (isEnabled()) {
            long delay = peakCalculator.calculateDelay(peakProfile, LocalTime.now());
            if (delay > 0) {
                log.info("Sleeping for {} ms", delay);
                sleeper.accept(delay);
            }
        }
        return callable.call();
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {

        LocalTime startTime = parseField(config, "startTime");
        LocalTime endTime = parseField(config, "endTime");
        List<Integer> midpoints = (List<Integer>)config.get("delayMidpoints");

        PeakProfile profile = PeakProfile.builder()
                .startTime(startTime)
                .endTime(endTime)
                .delayMidpoints(midpoints)
                .build();

        Set<ConstraintViolation<PeakProfile>> constraints = validator.validate(profile);

        if (!constraints.isEmpty()) {
            throw new ConstraintViolationException(this.getClass().getName()+" validation failed", constraints);
        }

        this.peakProfile = profile;

    }

    private LocalTime parseField(Map<String, Object> config, String fieldName) {
        try {
            String value = (String) config.get(fieldName);
            String[] split = value.split(":");
            if (split.length != 3) {
                throw new UnparseableValueException(fieldName, new IllegalArgumentException(fieldName+" must have format 'dd:dd:dd'. Wrong value: "+value));
            }
            return LocalTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), 0);
        } catch (NumberFormatException nfe) {
            throw new UnparseableValueException(fieldName, nfe);
        }
    }

}