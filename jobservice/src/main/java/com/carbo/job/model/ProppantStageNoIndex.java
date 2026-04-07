package com.carbo.job.model;

import com.carbo.proppantstage.model.ProppantContainer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.Date;

public class ProppantStageNoIndex {
    @Id
    private String id;

    @Field("date")
    private Date date;

    @Field("well")
    private String well;

    @Field("stage")
    private Float stage;

    @Field("silos")
    private Map<String, ProppantContainer> silos = new HashMap<>();

    @Field("runOrders")
    private List<ProppantContainer> runOrders = new ArrayList<>();

    @Field("blender")
    private String blender;

    @Field("diverter")
    private String diverter;

    @Field("diverterAmount")
    private Float diverterAmount;

    @Field("currentInSilos")
    private Map<String, Float> currentInSilos = new HashMap<>();

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public Float getStage() {
        return stage;
    }

    public void setStage(Float stage) {
        this.stage = stage;
    }

    public Map<String, ProppantContainer> getSilos() {
        return silos;
    }

    public void setSilos(Map<String, ProppantContainer> silos) {
        this.silos = silos;
    }

    public String getBlender() {
        return blender;
    }

    public void setBlender(String blender) {
        this.blender = blender;
    }

    public String getDiverter() {
        return diverter;
    }

    public void setDiverter(String diverter) {
        this.diverter = diverter;
    }

    public Float getDiverterAmount() {
        return diverterAmount;
    }

    public void setDiverterAmount(Float diverterAmount) {
        this.diverterAmount = diverterAmount;
    }

    public Map<String, Float> getCurrentInSilos() {
        return currentInSilos;
    }

    public void setCurrentInSilos(Map<String, Float> currentInSilos) {
        this.currentInSilos = currentInSilos;
    }

    public List<ProppantContainer> getRunOrders() {
        return runOrders;
    }

    public void setRunOrders(List<ProppantContainer> runOrders) {
        this.runOrders = runOrders;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }
}
