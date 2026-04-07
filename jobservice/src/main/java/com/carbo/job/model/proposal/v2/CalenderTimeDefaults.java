package com.carbo.job.model.proposal.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalenderTimeDefaults {
    double swapOverTime;

    double maintenance;

    double travelTo;

    double travelFrom;
}
