package com.carbo.job.model.proposal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "proposal")
@CompoundIndex(def = "{'_id': 1, 'job': 1}", name = "job_id_user_id_index", unique = true)
public class Proposal {
    @Id
    private String id;

    @Field("jobId")
    private String jobId;

    @Field("internalPropsal")
    private Integer internalPropsal;
    
    @Field("equipmentCharges")
    private List<FieldTicketLineItemProposal> equipmentCharges;

    @Field("otherCharges")
    private List<OtherCharges> otherCharges;

    @Field("discounts")
    private List<ChemicalDiscount> discounts;

    @Field("fileName")
    private String fileName;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field ("wellId")
    private String wellId;

    public Proposal(String jobId, Integer internalPropsal) {
        this.jobId = jobId;
        this.internalPropsal = internalPropsal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getInternalPropsal() {
        return internalPropsal;
    }

    public void setInternalPropsal(Integer internalPropsal) {
        this.internalPropsal = internalPropsal;
    }

    public List<FieldTicketLineItemProposal> getEquipmentCharges() {
        return equipmentCharges;
    }

    public void setEquipmentCharges(List<FieldTicketLineItemProposal> equipmentCharges) {
        this.equipmentCharges = equipmentCharges;
    }

    public List<OtherCharges> getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(List<OtherCharges> otherCharges) {
        this.otherCharges = otherCharges;
    }

    public List<ChemicalDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ChemicalDiscount> discounts) {
        this.discounts = discounts;
    }

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
