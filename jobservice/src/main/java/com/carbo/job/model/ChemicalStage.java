package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "chemical-stages")
public class ChemicalStage {
    @Id
    private String id;

    @Field("jobId")
    @NotNull
    @Indexed(unique = false)
    private String jobId;

    @Field("wellId")
    @Indexed(unique = false)
    @NotNull
    private String wellId;

    @Field("date")
    private Date date;

    @Field("well")
    private String well;

    @Field("stage")
    @NotNull
    private Float stage;

    @Field("chemicalAdditionUnit1")
    private List<Strap> chemicalAdditionUnit1 = new ArrayList<>();

    @Field("chemicalAdditionUnit2")
    private List<Strap> chemicalAdditionUnit2 = new ArrayList<>();

    @Field("isosTransport")
    private List<Strap> isosTransport = new ArrayList<>();

    @Field("dryAdd")
    private List<Strap> dryAdd = new ArrayList<>();

    @Field("cleanTotal")
    private Float cleanTotal;

    @Field("organizationId")
    @NotNull
    @Indexed(unique = false)
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    @Field("isMigrated")
    private Boolean isMigrated;

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

    public List<Strap> getChemicalAdditionUnit1() {
        return chemicalAdditionUnit1;
    }

    public void setChemicalAdditionUnit1(List<Strap> chemicalAdditionUnit1) {
        this.chemicalAdditionUnit1 = chemicalAdditionUnit1;
    }

    public List<Strap> getChemicalAdditionUnit2() {
        return chemicalAdditionUnit2;
    }

    public void setChemicalAdditionUnit2(List<Strap> chemicalAdditionUnit2) {
        this.chemicalAdditionUnit2 = chemicalAdditionUnit2;
    }

    public List<Strap> getIsosTransport() {
        return isosTransport;
    }

    public void setIsosTransport(List<Strap> isosTransport) {
        this.isosTransport = isosTransport;
    }

    public List<Strap> getDryAdd() {
        return dryAdd;
    }

    public void setDryAdd(List<Strap> dryAdd) {
        this.dryAdd = dryAdd;
    }

    public Float getCleanTotal() {
        return cleanTotal;
    }

    public void setCleanTotal(Float cleanTotal) {
        this.cleanTotal = cleanTotal;
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

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Long getCreated() {
        return created;
    }

    public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }
}
