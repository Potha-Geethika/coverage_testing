package com.carbo.job.events.model;

public class WellChangeModel {
    private String type;
    private String action;
    private String organizationId;
    private String wellName;
    private String wellId;
    private String wellAPI;
    private String wellAFE;
    private Integer totalStages;
    private Integer fracproId;

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getWellName() {
        return wellName;
    }

    public String getWellId() {
        return wellId;
    }

    public String getWellAPI() {
        return wellAPI;
    }

    public String getWellAFE() { return wellAFE; }

    public Integer getTotalStages() {
        return totalStages;
    }

    public Integer getFracproId() {
        return fracproId;
    }
}
