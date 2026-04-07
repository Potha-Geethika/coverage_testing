package com.carbo.job.model.simplified;

public class Material {
    private String name;
    private Float designed;
    private Float actual;
    private String uom;

    public Material(String name, Float designed, Float actual, String uom) {
        this.name = name;
        this.designed = designed;
        this.actual = actual;
        this.uom = uom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getDesigned() {
        return designed;
    }

    public void setDesigned(Float designed) {
        this.designed = designed;
    }

    public Float getActual() {
        return actual;
    }

    public void setActual(Float actual) {
        this.actual = actual;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
