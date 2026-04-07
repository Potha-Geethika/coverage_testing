package com.carbo.job.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.ObjectUtils;

public class FieldTicketVersion {
    @Field("version")
    private Integer version;

    @Field("status")
    private String status;

    @Field("chemicalCharges")
    private List<FieldTicketLineItem> chemicalCharges = new ArrayList<>();

    @Field("equipmentCharges")
    private List<FieldTicketLineItem> equipmentCharges = new ArrayList<>();

    @Field("mileageCharges")
    private List<FieldTicketLineItem> mileageCharges = new ArrayList<>();

    @Field("fuelCharges")
    private List<FieldTicketLineItem> fuelCharges = new ArrayList<>();

    @Field("miscCharges")
    private List<FieldTicketLineItem> miscCharges = new ArrayList<>();

    @Field("proppantCharges")
    private List<FieldTicketLineItem> proppantCharges = new ArrayList<>();

    @Field("chargeGroups")
    private Map<String, List<FieldTicketLineItem>> chargeGroups = new HashMap<>();

    @Field("approvedDate")
    private Long approvedDate;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public List<FieldTicketLineItem> getChemicalCharges() {
        return chemicalCharges;
    }

    public void setChemicalCharges(List<FieldTicketLineItem> chemicalCharges) {
        this.chemicalCharges = chemicalCharges;
    }

    public List<FieldTicketLineItem> getEquipmentCharges() {
        return equipmentCharges;
    }

    public void setEquipmentCharges(List<FieldTicketLineItem> equipmentCharges) {
        this.equipmentCharges = equipmentCharges;
    }

    public List<FieldTicketLineItem> getMileageCharges() {
        return mileageCharges;
    }

    public void setMileageCharges(List<FieldTicketLineItem> mileageCharges) {
        this.mileageCharges = mileageCharges;
    }

    public List<FieldTicketLineItem> getProppantCharges() {
        return proppantCharges;
    }

    public void setProppantCharges(List<FieldTicketLineItem> proppantCharges) {
        this.proppantCharges = proppantCharges;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Long approvedDate) {
        this.approvedDate = approvedDate;
    }

    public List<FieldTicketLineItem> getFuelCharges() {
        return fuelCharges;
    }

    public void setFuelCharges(List<FieldTicketLineItem> fuelCharges) {
        this.fuelCharges = fuelCharges;
    }

    public List<FieldTicketLineItem> getMiscCharges() {
        return miscCharges;
    }

    public void setMiscCharges(List<FieldTicketLineItem> miscCharges) {
        this.miscCharges = miscCharges;
    }

    public Map<String, List<FieldTicketLineItem>> getChargeGroups() { return chargeGroups; }

    public void setChargeGroups(Map<String, List<FieldTicketLineItem>> chargeGroups) { this.chargeGroups = chargeGroups; }
    
    private static Float getChargesNet(List<FieldTicketLineItem> charges) {
        if (charges.isEmpty()) {
            return 0.0f;
        }
        else {
            return charges.stream()
                    .map(x -> x.getNet())
                    .reduce(0.0f, Float::sum);
        }
    }

    public Float getChemicalChargesNet() {
        return getChargesNet(chemicalCharges);
    }

    public Float getEquipmentChargesNet() {
        return getChargesNet(equipmentCharges);
    }

    public Float getProppantChargesNet() {
        return getChargesNet(proppantCharges);
    }

    public Float getMileageChargesNet() {
        return getChargesNet(mileageCharges);
    }

    public Float getFuelChargesNet() {
        return getChargesNet(fuelCharges);
    }

    public Float getMiscChargesNet() {
        return getChargesNet(miscCharges);
    }

    public Float getProppantTons() {
        List<FieldTicketLineItem> fromNewStructure = chargeGroups.getOrDefault("proppantCharges", new ArrayList<>());

        if (ObjectUtils.isEmpty(proppantCharges) &&
                ObjectUtils.isEmpty(fromNewStructure)) {
            return 0.0f;
        }

        List<FieldTicketLineItem> all = new ArrayList<>();
        all.addAll(proppantCharges);
        all.addAll(fromNewStructure);

        float totalPounds = all.stream()
                .map(item -> convertToPounds(item.getQuantity(), item.getUom()))
                .reduce(0.0f, Float::sum);

        return totalPounds / 2000.0f;
    }

    private Float convertToPounds(Float quantity, String uom) {
        if (ObjectUtils.isEmpty(quantity) || ObjectUtils.isEmpty(uom)){
            return 0.0f;
        } else if(uom.equalsIgnoreCase("ton")){
            return quantity * 2000.0f;
        }else{
            return quantity;
        }
    }

    public Float getTonCharges() {
        if (proppantCharges.isEmpty()) {
            return 0.0f;
        }
        else {
            return proppantCharges.stream()
                    .filter(x -> !x.getDescription().startsWith("Customer Supplied Proppant"))
                    .map(x -> x.getQuantity())
                    .reduce(0.0f, Float::sum)/2000.0f;
        }
    }

    public boolean isApprovedBetween(long startDateTime, Long endDateTime) {
        if (approvedDate == null) {
            return false;
        }
        if (endDateTime == null) {
            return approvedDate >= startDateTime;
        }
        else {
            return approvedDate >= startDateTime && approvedDate < endDateTime;
        }
    }
}
