package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "on-site-equipments")
public class OnSiteEquipment {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @NotNull(message = "name can not be Null")
    private String name;

    @Field("dfName")
    private String dfName;

    @Field("type")
    @NotEmpty(message = "subtype can not be empty")
    @NotNull(message = "subtype can not be null")
    private String type;

    @Field("fleetId")
    private String fleetId;

    @Field("location")
    private String location;

    @Field("standby")
    private boolean standby;

    @Field("tier")
    private String tier;

    @Field("engines")
    private String engines;

    @Field("engineHours")
    private Integer engineHour;

    @Field("duelFuel")
    private boolean duelFuel;

    @Field("eku")
    private boolean eku;

    @Field("aoi")
    private boolean aoi;

    @Field("size")
    private Float size;

    @Field("plungerSize")
    private String plungerSize;

    @Field("wireless")
    private boolean wireless;

    @Field("engineRebuild")
    private boolean engineRebuild;

    @Field("status")
    private String status;

    @Field("yardStatus")
    private String yardStatus;

    @Field("hardDownStatus")
    private String hardDownStatus;

    @Field("comments")
    private String comments;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("organizationId")
    private String organizationId;

    @Field("currentHour")
    private Float currentHour;

    @Field("modifiedBy")
    private String modifiedBy;

    @Field("newAddStatus")
    private String newAddStatus;

    @Field("date")
    private String date;

    @Field("transmission")
    private String transmission;

    @Field("strokeLength")
    private String strokeLength;

    @Field("noOfPlungers")
    private String noOfPlungers;

    @Field("trailerAxles")
    private String trailerAxles;

    @Field("pumpIronBrandColor")
    private String pumpIronBrandColor;

    @Field("pumpStopped")
    private boolean pumpStopped = true;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("fluidEndBrand")
    private String fluidEndBrand;

    @Field("cleanHour")
    private Float cleanHour;

    @Field("dirtyHour")
    private Float dirtyHour;

    @Field("standByHour")
    private Float standByHour;

    @Field("referenceId")
    private String referenceId;

    @Field("hpp")
    private String hpp;

    @Field("mpn")
    private String mpn;

    @Field("connector")
    private String connector;

    @Field("singleOrDouble")
    private String singleOrDouble;

    @Field("length")
    private String length;

    @Field("manufacturer")
    private String manufacturer;

    @Field("horsepower")
    private String horsepower;

    @Field("rental")
    private String rental;

    @Field("blenderRate")
    private String blenderRate;

    @Field("controls")
    private String controls;

    @Field("hhp")
    private String hhp;

    @Field("standbyPump")
    private boolean standbyPump = false;

    @Field("rentalStartDate")
    private Long rentalStartDate;

    @Field("rentalEndDate")
    private Long rentalEndDate;

    @Field("fleetAssignDate")
    private Long fleetAssignDate;

    @Field("fleetAssignEndDate")
    private Long fleetAssignEndDate;
    @Field("maintenanceHour")
    private String maintenanceHour;
    @Field("splitStreamType")
    private String splitStreamType;
    @Field("bank")
    private String bank;

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

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
        if (name != null && !name.trim().isEmpty() && !name.trim().equalsIgnoreCase("null")) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("onsite_name cannot be null, empty, or  \"null\" ");
        }
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null && !type.trim().isEmpty() && !type.trim().equalsIgnoreCase("null") && !type.trim().equalsIgnoreCase("undefineds")) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("onsite_type can not be null, empty or \"null\"");
        }
    }

    public String getFleetId() {
        return fleetId;
    }

    public void setFleetId(String fleetId) {
        this.fleetId = fleetId;
    }

    public boolean getStandby() {
        return standby;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public Float getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(Float currentHour) {
        this.currentHour = currentHour;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getDfName() {
        return dfName;
    }

    public void setDfName(String dfName) {
        this.dfName = dfName;
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

    public boolean getDuelFuel() {
        return duelFuel;
    }

    public boolean getEku() {
        return eku;
    }

    public boolean getAoi() {
        return aoi;
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

    public boolean getWireless() {
        return wireless;
    }

    public boolean getEngineRebuild() {
        return engineRebuild;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isDuelFuel() {
        return duelFuel;
    }

    public void setDuelFuel(boolean duelFuel) {
        this.duelFuel = duelFuel;
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

    public boolean getPumpStopped() {
        return pumpStopped;
    }

    public String getFluidEndBrand() {
        return fluidEndBrand;
    }

    public void setFluidEndBrand(String fluidEndBrand) {
        this.fluidEndBrand = fluidEndBrand;
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

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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

    public String getHpp() {
        return hpp;
    }

    public void setHpp(String hpp) {
        this.hpp = hpp;
    }

    public boolean isPumpStopped() {
        return pumpStopped;
    }

    public void setPumpStopped(boolean pumpStopped) {
        this.pumpStopped = pumpStopped;
    }

    public String getMpn() {
        return mpn;
    }

    public void setMpn(String mpn) {
        this.mpn = mpn;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public String getSingleOrDouble() {
        return singleOrDouble;
    }

    public void setSingleOrDouble(String singleOrDouble) {
        this.singleOrDouble = singleOrDouble;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(String horsepower) {
        this.horsepower = horsepower;
    }

    public String getRental() {
        return rental;
    }

    public void setRental(String rental) {
        this.rental = rental;
    }

    public String getBlenderRate() {
        return blenderRate;
    }

    public void setBlenderRate(String blenderRate) {
        this.blenderRate = blenderRate;
    }

    public String getControls() {
        return controls;
    }

    public void setControls(String controls) {
        this.controls = controls;
    }

    public String getHhp() {
        return hhp;
    }

    public void setHhp(String hhp) {
        this.hhp = hhp;
    }

    public boolean isStandbyPump() {
        return standbyPump;
    }

    public void setStandbyPump(boolean standbyPump) {
        this.standbyPump = standbyPump;
    }

    public String getMaintenanceHour() {
        return maintenanceHour;
    }

    public void setMaintenanceHour(String maintenanceHour) {
        this.maintenanceHour = maintenanceHour;
    }
    public String getSplitStreamType() {return splitStreamType;}

    public void setSplitStreamType(String splitStreamType) {this.splitStreamType = splitStreamType;}
    public String getBank() {return bank;}
    public void setBank(String bank) {this.bank = bank;}

    public Long getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(Long rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public Long getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(Long rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public Long getFleetAssignDate() {
        return fleetAssignDate;
    }

    public void setFleetAssignDate(Long fleetAssignDate) {
        this.fleetAssignDate = fleetAssignDate;
    }

    public Long getFleetAssignEndDate() {
        return fleetAssignEndDate;
    }

    public void setFleetAssignEndDate(Long fleetAssignEndDate) {
        this.fleetAssignEndDate = fleetAssignEndDate;
    }
}
