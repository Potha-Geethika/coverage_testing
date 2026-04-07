package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Chemical {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("types")
    private List<Integer> types;

    @Field("subtype")
    private String subtype;

    @Field("concentration")
    private String concentration;

    @Field("volumePerStage")
    public Float volumePerStage;

    @Field("totalCleanVolume")
    public Float totalCleanVolume;

    @Field("totalCleanVolumeRound")
    public BigInteger totalCleanVolumeRound;

    @Field("uom")
    private String uom;

    @Field("code")
    private String code;

    @Field("price")
    private Float price;

    @Field("discount")
    private Float discount;

    @Field("description")
    private String description;

    @Field("calculatedVolume")
    private CalculatedVolume calculatedVolume;

    @Field ("isCustomerSupplied")
    private boolean isCustomerSupplied;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    public Chemical(){}
    public Chemical(Chemical chemical){
        this.id = chemical.getId();
        this.name = chemical.getName();
        this.code = chemical.getCode();
        this.subtype = chemical.getSubtype();
        this.concentration = chemical.getConcentration();
        this.volumePerStage = chemical.getVolumePerStage();
        this.totalCleanVolume = chemical.getTotalCleanVolume();
        this.totalCleanVolumeRound = chemical.getTotalCleanVolumeRound();
        this.uom = chemical.getUom();
        this.price = chemical.getPrice();
        this.discount = chemical.getDiscount();
        this.description = chemical.getDescription();
        this.isCustomerSupplied = chemical.getIsCustomerSupplied();
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
        this.name = name;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Float getVolumePerStage() {
        return volumePerStage;
    }

    public void setVolumePerStage(Float volumePerStage) {
        this.volumePerStage = volumePerStage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getDiscount() {
        return discount;
    }


    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getTotalCleanVolume() {
        return totalCleanVolume;
    }


    public void setTotalCleanVolume(Float totalCleanVolume) {
        this.totalCleanVolume = totalCleanVolume;
    }

    public BigInteger getTotalCleanVolumeRound() {
        return totalCleanVolumeRound;
    }


    public void setTotalCleanVolumeRound(BigInteger totalCleanVolumeRound) {
        this.totalCleanVolumeRound = totalCleanVolumeRound;
    }

    public CalculatedVolume getCalculatedVolume() {
        return calculatedVolume;
    }

    public void setCalculatedVolume(CalculatedVolume calculatedVolume) {
        this.calculatedVolume = calculatedVolume;
    }

    public boolean getIsCustomerSupplied() {
        return isCustomerSupplied;
    }

    public void setIsCustomerSupplied(boolean isCustomerSupplied) {
        this.isCustomerSupplied = isCustomerSupplied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chemical chemical = (Chemical) o;
        return name.equals(chemical.name);
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
