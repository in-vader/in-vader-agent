package com.github.invader.agent.interceptors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

/**
 * Definition of a peak profile.
 * @see PeakInterceptor
 *
 * Created by Jacek on 2017-07-01.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeakProfile {

    private LocalTime startTime;

    private LocalTime endTime;

    private List<Integer> delayMidpoints;

}
