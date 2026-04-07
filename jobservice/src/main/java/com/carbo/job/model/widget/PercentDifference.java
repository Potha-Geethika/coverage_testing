package com.carbo.job.model.widget;

public class PercentDifference extends ChemicalResponse {
    private float percentDiff;

    public PercentDifference(float design, float actual, float percentDiff) {
        super(design, actual);
        this.percentDiff = percentDiff;
    }

    public PercentDifference(float percentDiff) {
        this.percentDiff = percentDiff;
    }

    public PercentDifference() {

    }

    public float getPercentDiff() {
        return percentDiff;
    }

    public void setPercentDiff(float percentDiff) {
        this.percentDiff = percentDiff;
    }
}
