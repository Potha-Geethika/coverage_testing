package com.carbo.job.model.proposal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@Document (collection = "proposal-basic-information-v2")
@CompoundIndex (name = "unique_proposalid_organizationid_index", def = "{'proposalId': 1, 'organizationId': 1}", unique = true)
public class BasicInformation {
    @Id
    private String id;

    @Field ("rpfInformation")
    private RpfInformation rpfInformation;

    @Field ("customerContact")
    private CustomerContact customerContact;

    @Field ("preparedBy")
    private PreparedBy preparedBy;

    @Field ("operationRepresentative")
    private PreparedBy operationRepresentative;

    @Field ("status")
    private ProposalStateEnum status;

    @Field ("proposalId")
    private String proposalId;

    @Field ("organizationId")
    private String organizationId;

    @Field ("auditDetails")
    private AuditDetails auditDetails;

}
