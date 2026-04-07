package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.*;

public class SimplifiedWell {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("api")
    @NotEmpty(message = "api can not be empty")
    @Size(max = 14, message = "api can not be more than 14 characters.")
    private String api;

    @Field("afeNumber")
    @NotEmpty(message = "afeNumber can not be empty")
    @Size(max = 20, message = "afeNumber can not be more than 20 characters.")
    private String afeNumber;

    @Field("totalStages")
    private int totalStages;

    @Field("acidAdditives")
    private List<Chemical> acidAdditives = new ArrayList<>();

    @Field("slickwaters")
    private List<Chemical> slickwaters = new ArrayList<>();

    @Field("linearGelCrosslinks")
    private List<Chemical> linearGelCrosslinks = new ArrayList<>();

    @Field("diverters")
    private List<Chemical> diverters = new ArrayList<>();

    @Field("additionalChemicalTypes")
    private Map<String, List<Chemical>> additionalChemicalTypes = new HashMap<>();

    @Field("proppants")
    private List<Proppant> proppants = new ArrayList<>();

    @Field("fracproId")
    private int fracproId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAfeNumber() {
        return afeNumber;
    }

    public void setAfeNumber(String afeNumber) {
        this.afeNumber = afeNumber;
    }

    public int getTotalStages() {
        return totalStages;
    }

    public void setTotalStages(int totalStages) {
        this.totalStages = totalStages;
    }

    public List<Chemical> getAcidAdditives() {
        return acidAdditives;
    }

    public void setAcidAdditives(List<Chemical> acidAdditives) {
        this.acidAdditives = acidAdditives;
    }

    public List<Chemical> getSlickwaters() {
        return slickwaters;
    }

    public void setSlickwaters(List<Chemical> slickwaters) {
        this.slickwaters = slickwaters;
    }

    public List<Chemical> getLinearGelCrosslinks() {
        return linearGelCrosslinks;
    }

    public void setLinearGelCrosslinks(List<Chemical> linearGelCrosslinks) {
        this.linearGelCrosslinks = linearGelCrosslinks;
    }

    public Map<String, List<Chemical>> getAdditionalChemicalTypes() {
        return additionalChemicalTypes;
    }

    public void setAdditionalChemicalTypes(Map<String, List<Chemical>> additionalChemicalTypes) {
        this.additionalChemicalTypes = additionalChemicalTypes;
    }

    public List<Proppant> getProppants() {
        return proppants;
    }

    public void setProppants(List<Proppant> proppants) {
        this.proppants = proppants;
    }

    public int getFracproId() {
        return fracproId;
    }

    public void setFracproId(int fracproId) {
        this.fracproId = fracproId;
    }

    public List<Chemical> getDiverters() {
        return diverters;
    }

    public void setDiverters(List<Chemical> diverters) {
        this.diverters = diverters;
    }
}
