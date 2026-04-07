package com.carbo.job.model.widget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pricebook-components-v2")
@CompoundIndex(name = "unique_pricebookid_itemcode_pricebooktypeenum_index", def = "{'priceBookId': 1, 'itemCode': 1, 'priceBookTypeEnum': 1}", unique = true)
@CompoundIndex(name = "unique_pricebookid_name_pricebooktypeenum_index", def = "{'priceBookId': 1, 'name': 1, 'priceBookTypeEnum': 1}", unique = true)
public class PriceBookComponents {
    @Id
    private String id;

    @NotEmpty(message = "priceBookId can not be empty")
    @Field("priceBookId")
    private String priceBookId;

    @Field("name")
    private String name;

    @Field("itemCode")
    private String itemCode;

    @Field("price")
    private String price;

    @Field("uom")
    private String uom;

    @Field("type")
    private Set<PriceBookComponentsTypeEnum> type;

    @Field("description")
    private String description;

    @Field("acid")
    private String acid;

    @Field("dilutionRate")
    private String dilutionRate;

    @Field("rate")
    private double rate;

    @Field("specificGravity")
    private double specificGravity;

    @Field("isCustomerSupplied")
    private boolean isCustomerSupplied = false;

    @Field("priceBookType")
    private PriceBookTypeEnum priceBookTypeEnum;

    @Field("organizationId")
    private String organizationId;

    @Field("auditDetails")
    private AuditDetails auditDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPriceBookId() {
        return priceBookId;
    }

    public void setPriceBookId(String priceBookId) {
        this.priceBookId = priceBookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Set<PriceBookComponentsTypeEnum> getType() {
        return type;
    }

    public void setType(Set<PriceBookComponentsTypeEnum> type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcid() {
        return acid;
    }

    public void setAcid(String acid) {
        this.acid = acid;
    }

    public String getDilutionRate() {
        return dilutionRate;
    }

    public void setDilutionRate(String dilutionRate) {
        this.dilutionRate = dilutionRate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSpecificGravity() {
        return specificGravity;
    }

    public void setSpecificGravity(double specificGravity) {
        this.specificGravity = specificGravity;
    }

    public boolean isCustomerSupplied() {
        return isCustomerSupplied;
    }

    public void setCustomerSupplied(boolean customerSupplied) {
        isCustomerSupplied = customerSupplied;
    }

    public PriceBookTypeEnum getPriceBookTypeEnum() {
        return priceBookTypeEnum;
    }

    public void setPriceBookTypeEnum(PriceBookTypeEnum priceBookTypeEnum) {
        this.priceBookTypeEnum = priceBookTypeEnum;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public AuditDetails getAuditDetails() {
        return auditDetails;
    }

    public void setAuditDetails(AuditDetails auditDetails) {
        this.auditDetails = auditDetails;
    }
}
