package com.carbo.job.model.fracpro;

import java.util.List;

public class FracProTreatmentFluidsForWellResponse {
    public Result result;

    public static class Result {
        public List<FracProTreatmentFluid> fluid;
    }
}
