package com.carbo.job.model.proposal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.carbo.job.model.Chemical;
import com.carbo.job.model.Proppant;

public class ProposalData {
    private int internal;
    private int totalStages;
    private double totalCleanVolume;
    private String proposalId;
    private String organizationId;
    private List<Chemical> additives;
    private List<Proppant> proppants;
    private Map<String, BigInteger> fluids;
    private List<PumpScheduleStage> pumpSchedules;
    private Map<String, List<Chemical>> fluidTypeChemicalMap;
    private List<ChemicalDiscount> discounts;
    private List<FieldTicketLineItemProposal> equipmentCharges;
    private List<OtherCharges> otherCharges;

    public ProposalData() {
    }

    public ProposalData(List<Proposal> proposal, String organizationId, int totalStages, double totalCleanVolume, List<Chemical> additives, List<Proppant> proppants) {
        this.organizationId = organizationId;
        this.internal = proposal !=null ? proposal.get(0).getInternalPropsal(): 0;
        this.totalStages = totalStages;
        this.totalCleanVolume = totalCleanVolume;
        this.additives = additives;
        this.proppants = proppants;
        this.equipmentCharges =  proposal!=null ? getEquipmentChargesList(proposal):null;
        this.otherCharges =  proposal!=null ? getOtherChargesList(proposal):null;
        this.discounts = proposal!=null ? proposal.iterator().next().getDiscounts():null;
        this.proposalId =  proposal!=null ? proposal.iterator().next().getId():null;
    }

    public ProposalData(Proposal proposal, String organizationId, int totalStages, double totalCleanVolume, List<Chemical> additives, List<Proppant> proppants) {
        this.organizationId = organizationId;
        this.internal = proposal !=null ? proposal.getInternalPropsal(): 0;
        this.totalStages = totalStages;
        this.totalCleanVolume = totalCleanVolume;
        this.additives = additives;
        this.proppants = proppants;
        this.equipmentCharges =  proposal!=null ? proposal.getEquipmentCharges():null;
        this.otherCharges =  proposal!=null ? proposal.getOtherCharges():null;
        this.discounts = proposal!=null ? proposal.getDiscounts():null;
        this.proposalId =  proposal!=null ? proposal.getId():null;
    }

    public int getTotalStages() {
        return totalStages;
    }
    public void setTotalStages(int totalStages) {
        this.totalStages = totalStages;
    }
    public String getProposalId() {
        return proposalId;
    }
    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }
    public double getTotalCleanVolume() {
        return totalCleanVolume;
    }
    public void setTotalCleanVolume(double totalCleanVolume) {
        this.totalCleanVolume = totalCleanVolume;
    }
    public List<Chemical> getAdditives() {
        return additives;
    }
    public void setAdditives(List<Chemical> additives) {
        this.additives = additives;
    }
    public List<Proppant> getProppants() {
        return proppants;
    }
    public void setProppants(List<Proppant> proppants) {
        this.proppants = proppants;
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

    public int getInternal() {
        return internal;
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Map<String, BigInteger> getFluids() {
        return fluids;
    }

    public void setFluids(Map<String, BigInteger> fluids) {
        this.fluids = fluids;
    }

    public Map<String, List<Chemical>> getFluidTypeChemicalMap() {
        return fluidTypeChemicalMap;
    }

    public void setFluidTypeChemicalMap(Map<String, List<Chemical>> fluidTypeChemicalMap) {
        this.fluidTypeChemicalMap = fluidTypeChemicalMap;
    }

    public List<PumpScheduleStage> getPumpSchedules() {
        return pumpSchedules;
    }

    public void setPumpSchedules(List<PumpScheduleStage> pumpSchedules) {
        this.pumpSchedules = pumpSchedules;
    }
    
    @Override
    public String toString() {
        return "ProposalData [additives=" + additives + ", equipmentCharges=" + equipmentCharges + ", internal="
                + internal + ", otherCharges=" + otherCharges + ", proppants=" + proppants + ", totalCleanVolume="
                + totalCleanVolume + ", totalStages=" + totalStages + "]";
    }

    public List<ChemicalDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<ChemicalDiscount> discounts) {
        this.discounts = discounts;
    }

    public List<FieldTicketLineItemProposal> getEquipmentChargesList(List<Proposal> proposals){
        List<FieldTicketLineItemProposal> equipmentCharges = new ArrayList<>();
        for(int i=0; i<proposals.size(); i++){
            for(int j=0; j<proposals.get(i).getEquipmentCharges().size(); j++) {
                equipmentCharges.add(proposals.get(i).getEquipmentCharges().get(j));
            }
        }
        return equipmentCharges;
    }

    public List<OtherCharges> getOtherChargesList(List<Proposal> proposals){
        List<OtherCharges> otherCharges = new ArrayList<>();
        for(int i=0; i<proposals.size(); i++){
            for(int j=0; j<proposals.get(i).getOtherCharges().size(); j++) {
                otherCharges.add(proposals.get(i).getOtherCharges().get(j));
            }
        }
        return otherCharges;
    }
}
