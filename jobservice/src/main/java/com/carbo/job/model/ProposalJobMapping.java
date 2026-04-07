package com.carbo.job.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Document(collection = "proposal-job-mapping")
public class ProposalJobMapping {

    @Id
    private String id;

    @NotBlank
    @Field("proposalId")
    private String proposalId;

    @NotBlank
    @Field("organizationId")
    private String organizationId;

    @Field("basicInformationId")
    private String basicInformationId;

    @Field("basicInformationStatus")
    private String basicInformationStatus;

    @Field("operatorId")
    private String operatorId;

    @Field("operatorIdStatus")
    private String operatorIdStatus;

    @Field("padId")
    private String padId;

    @Field("padIdStatus")
    private String padIdStatus;

    @Field("proposalWellId")
    private String proposalWellId;

    @Field("getProposalWellErrorMessage")
    private String getProposalWellErrorMessage;

    @Field("getProposalWellStatus")
    private String getProposalWellStatus;

    @Field("jobWellIds")
    private List<String> jobWellIds;

    @Field("createWellErrorMessage")
    private String createWellErrorMessage;

    @Field("createWellStatus")
    private String createWellStatus;

    @Field("proposalFleetStatus")
    private String proposalFleetStatus;

    @Field("proposalFleetErrorMessage")
    private String proposalFleetErrorMessage;

    @Field("jobId")
    private String jobId;

    @Field("createNewJobFromProposalStatus")
    private String createNewJobFromProposalStatus;

    @Field("createNewJobFromProposalErrorMessage")
    private String createNewJobFromProposalErrorMessage;

    @Field("wellsPerforationId")
    private List<String> wellsPerforationId;

    @Field("wellsCasingJobIds")
    private List<String> wellsCasingJobIds;

    @Field("wellDataPopulateErrorMessage")
    private String wellDataPopulateErrorMessage;

    @Field("wellDataPopulateStatus")
    private String wellDataPopulateStatus;

    @Field("operatorContactJobId")
    private String operatorContactJobId;

    @Field("operatorContactDataPopulateErrorMessage")
    private String operatorContactDataPopulateErrorMessage;

    @Field("operatorContactDataPopulateStatus")
    private String operatorContactDataPopulateStatus;

    @Field("errorMessage")
    private String errorMessage;

    @NotBlank
    @Field("status")
    private String status;
}
