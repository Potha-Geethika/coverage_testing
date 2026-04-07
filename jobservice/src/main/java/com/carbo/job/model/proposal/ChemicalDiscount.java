package com.carbo.job.model.proposal;

public class ChemicalDiscount {
    private String name;
    private int discount;

    public ChemicalDiscount(){}

    public ChemicalDiscount(String name, int discount ){
        this.name = name;
        this.discount = discount;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    
}
