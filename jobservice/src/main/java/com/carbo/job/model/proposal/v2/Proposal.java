package com.carbo.job.model.proposal.v2;

import java.util.HashSet;
import java.util.Set;

import com.carbo.job.model.proposal.AuditDetails;
import com.carbo.job.model.proposal.BasicInformation;
import com.carbo.job.model.proposal.ProposalStateEnum;
import com.carbo.job.model.proposal.Well;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "proposal-v2")
public class Proposal {
    @Id
    private String id;

    @Field("version")
    private int version = 1;

    @Field("role")
    private ProposalRoleEnum role;

    //    @Field ("fluids")
    //    private LinkedList<Fluids> fluids;
    //
    //    @Field ("proppants")
    //    private LinkedList<Proppants> proppants;

    @Field("awaiting")
    private ProposalAwaitingEnum awaiting;

    @Field("priceBy")
    private PriceByEnum priceByEnum;

    @Field("wellEquipments")
    private Set<String> wellEquipments;

    @Field("stageEquipments")
    private Set<String> stageEquipments;

    @Field("jobId")
    private String jobId;

    @Field("isJobInProgress")
    private boolean isJobInProgress = false;

    @Field("fleetType")
    private String fleetType;

    @Field("proposalAction")
    private ProposalActionEnum proposalAction;

    @Field("proposalStatus")
    private ProposalStateEnum proposalStatus = ProposalStateEnum.DRAFT;

    @Field("organizationId")
    private String organizationId;

    @Field("selectedTemplate")
    private String selectedTemplate;

    @DBRef
    @Field("basicInformation")
    private BasicInformation basicInformation;

    @Field("priceBookId")
    private String priceBookId;

    @DBRef
    @Field("wells")
    private Well wells;

    @Field("showProposalName")
    private String showProposalName;

    @Field("opportunity")
    private String opportunity;

    @Field("companyId")
    private String companyId;

    @Field("rejectionComments")
    private String rejectionComments;

    @DBRef
    @Field("design")
    private Design design;

    @Field("dueDate")
    private Long dueDate;

    @Field("validDate")
    private long validDate;

    @Field("requestedDate")
    private long requestedDate;

    @Field("reschedule")
    private boolean reschedule = false;

    @Field("sentToClient")
    private boolean sentToClient;

    @Field("district")
    private String district;

    @Field ("districtId")
    private String districtId;

    @Field ("calenderTimeDefaults")
    private CalenderTimeDefaults calenderTimeDefaults;

    @Field("auditDetails")
    private AuditDetails auditDetails;

    @Field ("statusAuditDetails")
    private AuditDetails statusAuditDetails;

    @Field ("userFavourties")
    private Set<String> userFavourties = new HashSet<>();

    @Field("companyPrimaryId")
    private String companyPrimaryId;

    @Field ("documentLinkId")
    private String documentLinkId;

    @Field("leadId")
    private String leadId;

    @Field("isDeleted")
    private boolean isDeleted=false;

}
