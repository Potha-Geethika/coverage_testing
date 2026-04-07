package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "cost")
public class Cost {
    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("jobId")
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("wellId")
    @NotEmpty(message = "well id can not be empty")
    private String wellId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("date")
    private Date date;

    @Field("costType")
    @NotEmpty(message = "cost type can not be empty")
    private String costType;

    @Field("charge")
    @NotEmpty(message = "charge can not be empty")
    private Float charge;

    @Field("code")
    @NotEmpty(message = "code can not be empty")
    private String code;

    @Field("description")
    private String description;

    @Field("vendor")
    @NotEmpty(message = "vendor can not be empty")
    private String vendor;

    @Field("unitCost")
    @NotEmpty(message = "unit cost can not be empty")
    private Float unitCost;

    @Field("quantity")
    @NotEmpty(message = "quantity can not be empty")
    private Float quantity;

    @Field("carryOver")
    @NotEmpty(message = "carry over can not be empty")
    private Boolean carryOver;

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

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getCostType() { return costType; }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Float getCharge() {
        return charge;
    }

    public void setCharge(Float charge) {
        this.charge = charge;
    }

    public Float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Float unitCost) {
        this.unitCost = unitCost;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Boolean getCarryOver() {
        return carryOver;
    }

    public void setCarryOver(Boolean carryOver) {
        this.carryOver = carryOver;
    }


}
