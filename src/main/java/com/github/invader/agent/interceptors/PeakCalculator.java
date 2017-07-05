package com.github.invader.agent.interceptors;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;

/**
 * Calculates delay based on PeakProfile definition and current time.
 *
 *
 * Created by Jacek on 2017-07-01.
 */
@Slf4j
public class PeakCalculator {

    /**
     * @param peakProfile - definition of the peak
     * @param time - current time
     * @return 0 if current time is outside of the peak profile hour
     * or the delay [millis] corresponding to the correct midpoint (proportionally to time)
     */
    public int calculateDelay(PeakProfile peakProfile, OffsetTime time) {

        if (time.isAfter(peakProfile.getStartTime())
                && time.isBefore(peakProfile.getEndTime())) {
            long intervalFromStart = peakProfile.getStartTime().until(time, ChronoUnit.MILLIS);
            long fullInterval = peakProfile.getStartTime().until(peakProfile.getEndTime(), ChronoUnit.MILLIS);
            double peakProgressFraction = (double)intervalFromStart / (double)fullInterval;
            int currentMidpoint = (int) (peakProgressFraction * peakProfile.getDelayMidpoints().size());
            log.info("At position "+currentMidpoint);
            return peakProfile.getDelayMidpoints().get(currentMidpoint);
        }
        return 0;
    }


}
