package com.carbo.job.model;

public class Info {
    private int id;
    private String bol;
    private String sandType;
    private String navId;
    private int status;
    private String po;
    private String truckNumber;
    private String vendor;
    private int weight;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getSandType() {
        return sandType;
    }

    public void setSandType(String sandType) {
        this.sandType = sandType;
    }

    public String getNavId() {
        return navId;
    }

    public void setNavId(String navId) {
        this.navId = navId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
