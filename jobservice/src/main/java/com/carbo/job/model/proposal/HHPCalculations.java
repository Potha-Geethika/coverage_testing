package com.carbo.job.model.proposal;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HHPCalculations {
    @Field ("perforationFriction")
    private double perforationFriction;

    @Field ("TVD")
    private double TVD;

    @Field ("frictionCoefficient")
    private double frictionCoefficient;

    @Field ("bottomPerforation")
    private double bottomPerforation;

    @Field ("topPerforation")
    private double topPerforation;

    @Field ("fracproGradient")
    private double fracproGradient;

    @Field ("pipeFriction")
    private double pipeFriction;

    @Field ("hydrostaticPressure")
    private double hydrostaticPressure;

    @Field ("estSurfaceTreatingPressure")
    private double estSurfaceTreatingPressure;

    @Field ("estimatedHydraulicHorsepower")
    private double estimatedHydraulicHorsepower;

    @Field ("maxRate")
    private double maxRate;

    @Field ("maxPressure")
    private double maxPressure;

    @Field ("pumpTime")
    private double pumpTime;

    @Field ("HHPEstimated")
    private double HHPEstimated;

    @Field ("bottomHoleTemp")
    private double bottomHoleTemp;

    @Field ("BHFP")
    private double BHFP;

    @Field ("HHP")
    private double HHP;
}
