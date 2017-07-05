package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.constraints.CorrectTimes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetTime;
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
@CorrectTimes
public class PeakProfile {

    @NotNull
    private OffsetTime startTime;

    @NotNull
    private OffsetTime endTime;

    @NotNull @Size(min = 1, message = "Please provide at least 1 midpoint")
    private List<Integer> delayMidpoints;

}
