package com.carbo.job.model.widget;

public class ChemicalResponse {
    private float design;
    private float actual;

    private int count;

    public ChemicalResponse(float design, float actual, int count) {
        this.design = design;
        this.actual = actual;
        this.count = count;
    }
    public ChemicalResponse(float design, float actual) {
        this.design = design;
        this.actual = actual;
    }

    public ChemicalResponse() {
    }

    public float getDesign() {
        return design;
    }

    public void setDesign(float design) {
        this.design = design;
    }

    public float getActual() {
        return actual;
    }

    public void setActual(float actual) {
        this.actual = actual;
    }

    public int getCount() {return count;}

    public void setCount(int count) {this.count = count;}
}
