package com.carbo.job.model.proposal.v2;

import java.util.Map;
import java.util.Set;

import com.carbo.job.model.proposal.AuditDetails;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document (collection = "proposal-design-v2")
public class Design {
    @Id
    private String id;

    @Field ("proposalId")
    private String proposalId;

    @Field ("oldDesignId")
    private String oldDesignId;

    @Field ("designName")
    private String designName;

    @Field("isRamped")
    private boolean isRamped;

    @Field ("jobDesignName")
    private String jobDesignName;

    @Field ("stages")
    private int stages;

    @Field ("equipment")
    private Set<String> equipment;
    //
    //    @Field ("equipmentWisePrices")
    //    private Set<EquipmentWisePrice> equipmentWisePrices;

    @Field ("equipmentWisePrices")
    private Map<String, EquipmentWisePrice> equipmentWisePrices;

    @Field ("lbFt")
    private String lbFt;

    @Field ("bblFt")
    private String bblFt;

    @DBRef
    @Field ("pumpSchedule")
    private PumpSchedule pumpSchedule;

    @Field ("fluidTotals")
    private Map<String, Double> fluidTotals;

    @Field ("additiveTotals")
    private Map<String, Double> additiveTotals;

    @Field ("totalStageTime")
    private double totalStageTime;

    @Field ("proppantTotals")
    private Map<String, Double> proppantTotals;

    @Field ("organizationId")
    private String organizationId;

    @Field ("designLibraryId")
    private String designLibraryId;

    @Field ("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty ("isSelected")
    @Field ("isSelected")
    private boolean isSelected;

    @JsonProperty ("isSelected")
    public boolean getIsSelected() {
        return isSelected;
    }

    @JsonProperty ("isSelected")
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isRamped() {
        return isRamped;
    }

    public void setRamped(boolean ramped) {
        isRamped = ramped;
    }

}