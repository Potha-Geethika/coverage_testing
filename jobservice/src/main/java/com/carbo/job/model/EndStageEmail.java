package com.carbo.job.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "emails")
public class EndStageEmail extends Email {
    @Field("well")
    private String well;

    @Field("stage")
    private String stage;

    @Field("pad")
    private String pad;

    @Field("startTime")
    private String startTime;

    @Field("finishTime")
    private String finishTime;

    @Field("pumpStart")
    private Integer pumpStart;

    @Field("pumpEnd")
    private Integer pumpEnd;

    @Field("fieldCoordinator")
    private String fieldCoordinator;

    @Field("serviceSupervisor")
    private String serviceSupervisor;

    @Field("targetStagesPerDay")
    private Integer targetStagesPerDay;

    @Field("actualStagesPerDay")
    private Integer actualStagesPerDay;

    @Field("averagePressure")
    private Float averagePressure;

    @Field("averageRate")
    private Float averageRate;

    @Field("totalCleanFluid")
    private Integer totalCleanFluid;

    @Field("blender")
    private String blender;

    @Field("additionalComments")
    private String additionalComments;

    @Field("formationName")
    private String formationName;

    @Field("type")
    protected EmailType type = getEmailType();

    @Field("diesel")
    private Float diesel;

    @Field("fieldGas")
    private Float fieldGas;

    @Field("cng")
    private Float cng;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("lng")
    private Float lng=0.0f;

    @Field("btu")
    private Float btu;

    @Field("producedWater")
    private Float producedWater;

    public Float getProducedWater() {
        return producedWater;
    }

    public void setProducedWater(Float producedWater) {
        this.producedWater = producedWater;
    }

    public Float getBtu() {
        return btu;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
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

    public String getFieldCoordinator() {
        return fieldCoordinator;
    }

    public void setFieldCoordinator(String fieldCoordinator) {
        this.fieldCoordinator = fieldCoordinator;
    }

    public String getServiceSupervisor() {
        return serviceSupervisor;
    }

    public void setServiceSupervisor(String serviceSupervisor) {
        this.serviceSupervisor = serviceSupervisor;
    }

    public Integer getTargetStagesPerDay() {
        return targetStagesPerDay;
    }

    public void setTargetStagesPerDay(Integer targetStagesPerDay) {
        this.targetStagesPerDay = targetStagesPerDay;
    }

    public Integer getActualStagesPerDay() {
        return actualStagesPerDay;
    }

    public void setActualStagesPerDay(Integer actualStagesPerDay) {
        this.actualStagesPerDay = actualStagesPerDay;
    }

    public Float getAveragePressure() {
        return averagePressure;
    }

    public void setAveragePressure(Float averagePressure) {
        this.averagePressure = averagePressure;
    }

    public Float getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Float averageRate) {
        this.averageRate = averageRate;
    }

    public Integer getTotalCleanFluid() {
        return totalCleanFluid;
    }

    public void setTotalCleanFluid(Integer totalCleanFluid) {
        this.totalCleanFluid = totalCleanFluid;
    }

    public String getBlender() {
        return blender;
    }

    public void setBlender(String blender) {
        this.blender = blender;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getFormationName() { return formationName; }

    public void setFormationName(String formationName) { this.formationName = formationName; }

    public EmailType getEmailType() {
        return EmailType.END_STAGE;
    }

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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setBtu(Float btu) {
        this.btu = btu;
    }
}
