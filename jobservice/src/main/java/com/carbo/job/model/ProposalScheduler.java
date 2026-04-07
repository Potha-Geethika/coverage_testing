package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "proposal-scheduler-v2")
@CompoundIndex(name = "unique_organizationid_proposalid_showProposalName_index", def = "{'organizationId': 1, 'proposalId': 1, 'showProposalName': 1}", unique = true)
public class ProposalScheduler {
    @Id
    private String id;

    @Field("proposalId")
    private String proposalId;

    @Field("showProposalName")
    private String showProposalName;

    @Field("organizationId")
    private String organizationId;

    @Field("districtId")
    private String districtId;

    @Field("districtName")
    private String districtName;

    @Field("fleetType")
    private String fleetType;

    @Field("fleetId")
    private String fleetId;

    @Field("scheduledStartDate")
    private long scheduledStartDate;

    @Field("scheduledEndDate")
    private long scheduledEndDate;

    @Field("estimatedDuration")
    private EstimatedDuration estimatedDuration;

    @Field("sendToSales")
    private long sendToSales;

    @Field("isScheduled")
    private boolean isScheduled;

    @Field("schedulerName")
    private String schedulerName;

    @Field("isPOSet")
    private boolean isPOSet;

    @Field("isVendorSet")
    private boolean isVendorSet;

    @Field("isFileUpload")
    private boolean isFileUpload;

    @Field("auditDetails")
    private AuditDetails auditDetails;

    @Field("reasonForDelay")
    private String reasonForDelay;

}