package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.OptionalDouble;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboadResponseTotalCards {

    private int jobTotal;
    private int operatorsTotal;
    private int wellsTotal;
    private int fleetsTotal;
    private double pumpTimeEfficiency;

}