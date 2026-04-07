package com.carbo.proppantstage.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Silo  extends ProppantContainer {
    @Field("startPercent")
    private Float startPercent;

    @Field("leavePercent")
    private Float leavePercent;

    @Field("coeff")
    private Float coeff;

    @Field("total")
    private Float total;

    @Field("designVolume")
    private Float designVolume;

    @Field("leaveLbs")
    private Float leaveLbs;

    @Field("calcSilo")
    private Float calcSilo;

    @Field("remainOnTickets")
    private Float remainOnTickets;

    @Field("runOrder")
    private String runOrder;

    @Field("actualEndingPercent")
    private Float actualEndingPercent;

    @Field("note")
    private String note;

    public Float getStartPercent() {
        return startPercent;
    }

    public void setStartPercent(Float startPercent) {
        this.startPercent = startPercent;
    }

    public Float getLeavePercent() {
        return leavePercent;
    }

    public void setLeavePercent(Float leavePercent) {
        this.leavePercent = leavePercent;
    }

    public Float getCoeff() {
        return coeff;
    }

    public void setCoeff(Float coeff) {
        this.coeff = coeff;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getDesignVolume() {
        return designVolume;
    }

    public void setDesignVolume(Float designVolume) {
        this.designVolume = designVolume;
    }

    public Float getLeaveLbs() {
        return leaveLbs;
    }

    public void setLeaveLbs(Float leaveLbs) {
        this.leaveLbs = leaveLbs;
    }

    public Float getCalcSilo() {
        return calcSilo;
    }

    public void setCalcSilo(Float calcSilo) {
        this.calcSilo = calcSilo;
    }

    public Float getRemainOnTickets() {
        return remainOnTickets;
    }

    public void setRemainOnTickets(Float remainOnTickets) {
        this.remainOnTickets = remainOnTickets;
    }

    public String getRunOrder() {
        return runOrder;
    }

    public void setRunOrder(String runOrder) {
        this.runOrder = runOrder;
    }

    public Float getActualEndingPercent() {
        return actualEndingPercent;
    }

    public void setActualEndingPercent(Float actualEndingPercent) {
        this.actualEndingPercent = actualEndingPercent;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
