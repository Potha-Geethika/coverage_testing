package com.carbo.job.model.simplified;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    private String jobNumber;
    private Float cleanTotal;
    private Integer pumpStart;
    private Integer pumpEnd;
    private Integer padStageTotal;
    private Integer padStageCompleted;

    private List<Material> chemicals = new ArrayList<>();
    private List<Material> proppants = new ArrayList<>();
    private List<Material> acids = new ArrayList<>();
    private Float diesel;

    private Float fieldGas;

    private Float cng;

    public Float getDiesel() {
        return diesel;
    }

    public void setDiesel(Float diesel) {
        this.diesel = diesel;
    }

    public Float getFieldGas() {
        return fieldGas;
    }

    public void setFieldGas(Float fieldGas) {
        this.fieldGas = fieldGas;
    }

    public Float getCng() {
        return cng;
    }

    public void setCng(Float cng) {
        this.cng = cng;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public List<Material> getChemicals() {
        return chemicals;
    }

    public void setChemicals(List<Material> chemicals) {
        this.chemicals = chemicals;
    }

    public List<Material> getProppants() {
        return proppants;
    }

    public void setProppants(List<Material> proppants) {
        this.proppants = proppants;
    }

    public List<Material> getAcids() {
        return acids;
    }

    public void setAcids(List<Material> acids) {
        this.acids = acids;
    }

    public void addChemical(Material material) {
        chemicals.add(material);
    }

    public void addAcid(Material material) {
        acids.add(material);
    }

    public void addProppant(Material material) {
        proppants.add(material);
    }

    public Float getCleanTotal() {
        return cleanTotal;
    }

    public void setCleanTotal(Float cleanTotal) {
        this.cleanTotal = cleanTotal;
    }

    public Integer getPumpStart() {
        return pumpStart;
    }

    public void setPumpStart(Integer pumpStart) {
        this.pumpStart = pumpStart;
    }

    public Integer getPumpEnd() {
        return pumpEnd;
    }

    public void setPumpEnd(Integer pumpEnd) {
        this.pumpEnd = pumpEnd;
    }

    public Integer getPadStageTotal() {
        return padStageTotal;
    }

    public void setPadStageTotal(Integer padStageTotal) {
        this.padStageTotal = padStageTotal;
    }

    public Integer getPadStageCompleted() {
        return padStageCompleted;
    }

    public void setPadStageCompleted(Integer padStageCompleted) {
        this.padStageCompleted = padStageCompleted;
    }
}
