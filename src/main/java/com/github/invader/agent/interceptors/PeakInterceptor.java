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
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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
                .startTime(OffsetTime.MIN)
                .endTime(OffsetTime.of(12,0,0,0, ZoneOffset.ofHours(0)))
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
            long delay = peakCalculator.calculateDelay(peakProfile, OffsetTime.now());
            if (delay > 0) {
                log.info("Sleeping for {} ms", delay);
                sleeper.accept(delay);
            }
        }
        return callable.call();
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {

        OffsetTime startTime = parseField(config, "startTime");
        OffsetTime endTime = parseField(config, "endTime");
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

    private OffsetTime parseField(Map<String, Object> config, String fieldName) {

        String offsetTimeValue = (String) config.get(fieldName);
        try {
            return OffsetTime.parse(offsetTimeValue);
        } catch (DateTimeParseException e) {
            throw new UnparseableValueException("Unparseable OffsetTime format for "+fieldName+"="+offsetTimeValue, e);
        }
    }

}
