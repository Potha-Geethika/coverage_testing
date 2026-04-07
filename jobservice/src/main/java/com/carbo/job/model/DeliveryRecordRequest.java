package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Size;
import java.util.Date;


public class DeliveryRecordRequest {
    @JsonProperty("dateTime")
    private Date dateTime;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("bol")
    private String bol;

    @JsonProperty("wtAmount")
    private float wtAmount;

    @JsonProperty("truckNumber")
    private String truckNumber;

    @JsonProperty("orderStatusID")
    private int orderStatusID;

    @JsonProperty("autoOrderId")
    private int autoOrderId;

    @JsonProperty("po")
    private String po;
    @JsonProperty("navId")
    private String navId;

    @JsonProperty("itemNo")
    private String itemNo;

    @JsonProperty("customerID")
    @Size(max = 30)
    private String customerID;

    @JsonProperty("eventTimes")
    private EventTimes eventTimes;

    public DeliveryRecordRequest() {
    }
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }


    public float getWtAmount() {
        return wtAmount;
    }

    public void setWtAmount(float wtAmount) {
        this.wtAmount = wtAmount;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    public int getOrderStatusID() {
        return orderStatusID;
    }

    public void setOrderStatusID(int orderStatusID) {
        this.orderStatusID = orderStatusID;
    }

    public int getAutoOrderId() {
        return autoOrderId;
    }

    public void setAutoOrderId(int autoOrderId) {
        this.autoOrderId = autoOrderId;
    }

    public String getNavId() {
        return navId;
    }

    public void setNavId(String navId) {
        this.navId = navId;
    }

    public String getItemNo() {
        return itemNo;}
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getCustomerID() {return customerID;}

    public void setCustomerID(String customerID) {this.customerID = customerID;}

    public EventTimes getEventTimes() {
        return eventTimes;
    }

    public void setEventTimes(EventTimes eventTimes) {
        this.eventTimes = eventTimes;
    }
}