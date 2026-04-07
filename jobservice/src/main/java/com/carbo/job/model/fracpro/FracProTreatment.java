package com.carbo.job.model.fracpro;

import java.util.Objects;

public class FracProTreatment {
    private String name;
    private String formationName;
    private Float totalVolume;
    private Float pumpDownVolume;
    private Float averagePres;
    private Float maxPres;
    private Float avgSlurryReturnRate;
    private Float maxFluidRate;
    private Float mdFormationTop;
    private Float mdFormationBottom;
    private Integer totalPerfs;
    private Float breakDownPres;
    private Float initialShutinPres;
    private Float fractureGradient;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormationName() {
        return formationName;
    }

    public void setFormationName(String formationName) {
        this.formationName = formationName;
    }

    public Float getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Float totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Float getPumpDownVolume() {
        return pumpDownVolume;
    }

    public void setPumpDownVolume(Float pumpDownVolume) {
        this.pumpDownVolume = pumpDownVolume;
    }

    public Float getAveragePres() {
        return averagePres;
    }

    public void setAveragePres(Float averagePres) {
        this.averagePres = averagePres;
    }

    public Float getMaxPres() {
        return maxPres;
    }

    public void setMaxPres(Float maxPres) {
        this.maxPres = maxPres;
    }

    public Float getAvgSlurryReturnRate() {
        return avgSlurryReturnRate;
    }

    public void setAvgSlurryReturnRate(Float avgSlurryReturnRate) {
        this.avgSlurryReturnRate = avgSlurryReturnRate;
    }

    public Float getMaxFluidRate() {
        return maxFluidRate;
    }

    public void setMaxFluidRate(Float maxFluidRate) {
        this.maxFluidRate = maxFluidRate;
    }

    public Float getMdFormationTop() {
        return mdFormationTop;
    }

    public void setMdFormationTop(Float mdFormationTop) {
        this.mdFormationTop = mdFormationTop;
    }

    public Float getMdFormationBottom() {
        return mdFormationBottom;
    }

    public void setMdFormationBottom(Float mdFormationBottom) {
        this.mdFormationBottom = mdFormationBottom;
    }

    public Integer getTotalPerfs() {
        return totalPerfs;
    }

    public void setTotalPerfs(Integer totalPerfs) {
        this.totalPerfs = totalPerfs;
    }

    public Float getBreakDownPres() {
        return breakDownPres;
    }

    public void setBreakDownPres(Float breakDownPres) {
        this.breakDownPres = breakDownPres;
    }

    public Float getInitialShutinPres() {
        return initialShutinPres;
    }

    public void setInitialShutinPres(Float initialShutinPres) {
        this.initialShutinPres = initialShutinPres;
    }

    public Float getFractureGradient() {
        return fractureGradient;
    }

    public void setFractureGradient(Float fractureGradient) {
        this.fractureGradient = fractureGradient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FracProTreatment that = (FracProTreatment) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
