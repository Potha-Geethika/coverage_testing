package com.carbo.job.model;


import java.util.Date;

public class EquipmentOverview {

    private String id;

    private String name;

    private Float currentHour;

    private String pumpType;

    private Float cleanHour;

    private Float dirtyHour;

    private Float standByHour;

    private boolean standby;

    private String dfName;

    private String type;

    private String fleetId;

    private String location;

    private String tier;

    private String engines;

    private Integer engineHour;

    private boolean duelFuel;

    private boolean eku;

    private boolean aoi;

    private Float size;

    private String plungerSize;

    private boolean wireless;

    private boolean engineRebuild;

    private String status;

    private String yardStatus;

    private String hardDownStatus;

    private String comments;

    private Long ts;

    private Long created = new Date().getTime();

    private Long modified = new Date().getTime();

    private String organizationId;

    private String modifiedBy;

    private String newAddStatus;

    private String date;

    private String transmission;

    private String strokeLength;

    private String noOfPlungers;

    private String trailerAxles;

    private String pumpIronBrandColor;

    private boolean pumpStopped = false;

    private String lastModifiedBy;

    private String fluidEndBrand;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(Float currentHour) {
        this.currentHour = currentHour;
    }

    public String getPumpType() {
        return pumpType;
    }

    public void setPumpType(String pumpType) {
        this.pumpType = pumpType;
    }

    public Float getCleanHour() {
        return cleanHour;
    }

    public void setCleanHour(Float cleanHour) {
        this.cleanHour = cleanHour;
    }

    public Float getDirtyHour() {
        return dirtyHour;
    }

    public void setDirtyHour(Float dirtyHour) {
        this.dirtyHour = dirtyHour;
    }

    public Float getStandByHour() {
        return standByHour;
    }

    public void setStandByHour(Float standByHour) {
        this.standByHour = standByHour;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public String getDfName() {
        return dfName;
    }

    public void setDfName(String dfName) {
        this.dfName = dfName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFleetId() {
        return fleetId;
    }

    public void setFleetId(String fleetId) {
        this.fleetId = fleetId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getEngines() {
        return engines;
    }

    public void setEngines(String engines) {
        this.engines = engines;
    }

    public Integer getEngineHour() {
        return engineHour;
    }

    public void setEngineHour(Integer engineHour) {
        this.engineHour = engineHour;
    }

    public boolean isDuelFuel() {
        return duelFuel;
    }

    public void setDuelFuel(boolean duelFuel) {
        this.duelFuel = duelFuel;
    }

    public boolean isEku() {
        return eku;
    }

    public void setEku(boolean eku) {
        this.eku = eku;
    }

    public boolean isAoi() {
        return aoi;
    }

    public void setAoi(boolean aoi) {
        this.aoi = aoi;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public String getPlungerSize() {
        return plungerSize;
    }

    public void setPlungerSize(String plungerSize) {
        this.plungerSize = plungerSize;
    }

    public boolean isWireless() {
        return wireless;
    }

    public void setWireless(boolean wireless) {
        this.wireless = wireless;
    }

    public boolean isEngineRebuild() {
        return engineRebuild;
    }

    public void setEngineRebuild(boolean engineRebuild) {
        this.engineRebuild = engineRebuild;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getYardStatus() {
        return yardStatus;
    }

    public void setYardStatus(String yardStatus) {
        this.yardStatus = yardStatus;
    }

    public String getHardDownStatus() {
        return hardDownStatus;
    }

    public void setHardDownStatus(String hardDownStatus) {
        this.hardDownStatus = hardDownStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getNewAddStatus() {
        return newAddStatus;
    }

    public void setNewAddStatus(String newAddStatus) {
        this.newAddStatus = newAddStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getStrokeLength() {
        return strokeLength;
    }

    public void setStrokeLength(String strokeLength) {
        this.strokeLength = strokeLength;
    }

    public String getNoOfPlungers() {
        return noOfPlungers;
    }

    public void setNoOfPlungers(String noOfPlungers) {
        this.noOfPlungers = noOfPlungers;
    }

    public String getTrailerAxles() {
        return trailerAxles;
    }

    public void setTrailerAxles(String trailerAxles) {
        this.trailerAxles = trailerAxles;
    }

    public String getPumpIronBrandColor() {
        return pumpIronBrandColor;
    }

    public void setPumpIronBrandColor(String pumpIronBrandColor) {
        this.pumpIronBrandColor = pumpIronBrandColor;
    }

    public boolean isPumpStopped() {
        return pumpStopped;
    }

    public void setPumpStopped(boolean pumpStopped) {
        this.pumpStopped = pumpStopped;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getFluidEndBrand() {
        return fluidEndBrand;
    }

    public void setFluidEndBrand(String fluidEndBrand) {
        this.fluidEndBrand = fluidEndBrand;
    }
}

