package com.carbo.job.model.proposal;

import org.springframework.data.mongodb.core.mapping.Field;

public class FieldTicketLineItemProposal {
    @Field("code")
    private String code;

    @Field("quantity")
    private Float quantity;

    @Field("description")
    private String description;

    @Field("price")
    private Float price;

    @Field("uom")
    private String uom;

    @Field("discount")
    private String discount;

    @Field("readOnly")
    private Boolean readOnly;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}
