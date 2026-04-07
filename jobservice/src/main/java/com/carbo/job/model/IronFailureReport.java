package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "iron-failure-reports")
public class IronFailureReport {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("date")
    @NotEmpty(message = "date can not be empty")
    private Date date;

    @Field("fleet")
    @NotEmpty(message = "fleet can not be empty")
    private String fleet;

    @Field("customer")
    @NotEmpty(message = "customer can not be empty")
    private String customer;

    @Field("pad")
    @NotEmpty(message = "pad can not be empty")
    private String pad;

    @Field("well")
    @NotEmpty(message = "well can not be empty")
    private String well;

    @Field("stage")
    @NotEmpty(message = "stage can not be empty")
    private String stage;

    @Field("location")
    @NotEmpty(message = "location can not be empty")
    private String location;

    @Field("totalNpt")
    private Long totalNpt;

    @Field("totalNptHours")
    private int totalNptHours;

    @Field("totalNptMinutes")
    private int totalNptMinutes;

    @Field("pressure")
    private int pressure;

    @Field("rate")
    private int rate;

    @Field("proppantConcentration")
    private float proppantConcentration;

    @Field("serviceSupervisors")
    private List<ServiceSupervisor> serviceSupervisors = new ArrayList<>();

    @Field("failedComponent")
    private String failedComponent;

    @Field("failurePoint")
    private String failurePoint;

    @Field("manufacturer")
    private String manufacturer;

    @Field("bandColor")
    private String bandColor;

    @Field("serialNumber")
    private String serialNumber;

    @Field("whereAssetWasRiggedIn")
    private String whereAssetWasRiggedIn;

    @Field("stationNumber")
    private String stationNumber;

    @Field("failedIronRiggedInPictureOne")
    private Photo failedIronRiggedInPictureOne;

    @Field("failedIronRiggedInPictureTwo")
    private Photo failedIronRiggedInPictureTwo;

    @Field("failurePointPictureOne")
    private Photo failurePointPictureOne;

    @Field("failurePointPictureTwo")
    private Photo failurePointPictureTwo;

    @Field("extraPictures")
    private List<Photo> extraPictures = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getFleet() {
        return fleet;
    }

    public void setFleet(String fleet) {
        this.fleet = fleet;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPad() {
        return pad;
    }

    public void setPad(String pad) {
        this.pad = pad;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getTotalNpt() {
        return totalNpt;
    }

    public void setTotalNpt(Long totalNpt) {
        this.totalNpt = totalNpt;
    }

    public int getTotalNptHours() {
        return totalNptHours;
    }

    public void setTotalNptHours(int totalNptHours) {
        this.totalNptHours = totalNptHours;
    }

    public int getTotalNptMinutes() {
        return totalNptMinutes;
    }

    public void setTotalNptMinutes(int totalNptMinutes) {
        this.totalNptMinutes = totalNptMinutes;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public float getProppantConcentration() {
        return proppantConcentration;
    }

    public void setProppantConcentration(float proppantConcentration) {
        this.proppantConcentration = proppantConcentration;
    }

    public List<ServiceSupervisor> getServiceSupervisors() {
        return serviceSupervisors;
    }

    public void setServiceSupervisors(List<ServiceSupervisor> serviceSupervisors) {
        this.serviceSupervisors = serviceSupervisors;
    }

    public String getFailedComponent() {
        return failedComponent;
    }

    public void setFailedComponent(String failedComponent) {
        this.failedComponent = failedComponent;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBandColor() {
        return bandColor;
    }

    public void setBandColor(String bandColor) {
        this.bandColor = bandColor;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getWhereAssetWasRiggedIn() {
        return whereAssetWasRiggedIn;
    }

    public void setWhereAssetWasRiggedIn(String whereAssetWasRiggedIn) {
        this.whereAssetWasRiggedIn = whereAssetWasRiggedIn;
    }

    public String getFailurePoint() {
        return failurePoint;
    }

    public void setFailurePoint(String failurePoint) {
        this.failurePoint = failurePoint;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(String stationNumber) {
        this.stationNumber = stationNumber;
    }

    public Photo getFailedIronRiggedInPictureOne() {
        return failedIronRiggedInPictureOne;
    }

    public void setFailedIronRiggedInPictureOne(Photo failedIronRiggedInPictureOne) {
        this.failedIronRiggedInPictureOne = failedIronRiggedInPictureOne;
    }

    public Photo getFailedIronRiggedInPictureTwo() {
        return failedIronRiggedInPictureTwo;
    }

    public void setFailedIronRiggedInPictureTwo(Photo failedIronRiggedInPictureTwo) {
        this.failedIronRiggedInPictureTwo = failedIronRiggedInPictureTwo;
    }

    public Photo getFailurePointPictureOne() {
        return failurePointPictureOne;
    }

    public void setFailurePointPictureOne(Photo failurePointPictureOne) {
        this.failurePointPictureOne = failurePointPictureOne;
    }

    public Photo getFailurePointPictureTwo() {
        return failurePointPictureTwo;
    }

    public void setFailurePointPictureTwo(Photo failurePointPictureTwo) {
        this.failurePointPictureTwo = failurePointPictureTwo;
    }

    public List<Photo> getExtraPictures() {
        return extraPictures;
    }

    public void setExtraPictures(List<Photo> extraPictures) {
        this.extraPictures = extraPictures;
    }
}
