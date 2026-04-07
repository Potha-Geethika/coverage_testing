package com.carbo.job.model.jobPanel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Coordinates {

    private double latitude;

    private double longitude;
}
