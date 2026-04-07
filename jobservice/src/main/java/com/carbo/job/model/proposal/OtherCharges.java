package com.carbo.job.model.proposal;

import java.util.List;

public class OtherCharges {
    private String name;
    private List<FieldTicketLineItemProposal> data;

    public OtherCharges(){}

    public OtherCharges(String name, List<FieldTicketLineItemProposal> data){
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<FieldTicketLineItemProposal> getData() {
        return data;
    }
    public void setData(List<FieldTicketLineItemProposal> data) {
        this.data = data;
    }
    
}
