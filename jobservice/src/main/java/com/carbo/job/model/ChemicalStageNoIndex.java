package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChemicalStageNoIndex {
    @Id
    private String id;

    @Field("date")
    private Date date;

    @Field("well")
    private String well;

    @Field("stage")
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
