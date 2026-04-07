package com.carbo.job.model;

import com.carbo.job.model.threed.ThreeDWell;
import com.carbo.job.model.well.StageInfo;
import com.carbo.ws.model.ProppantStage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@Document(collection = "wells")
@Data
public class Well {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    @Size(max = 100, message = "name can not be more than 100 characters.")
    private String name;

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;
    @Field("api")
    @NotEmpty(message = "api can not be empty")
    @Size(max = 14, message = "api can not be more than 14 characters.")
    private String api;

    @Field("longitude")
    private double longitude;

    @Field("latitude")
    private double latitude;

    @Field("afeNumber")
    @NotEmpty(message = "afeNumber can not be empty")
    @Size(max = 20, message = "afeNumber can not be more than 20 characters.")
    private String afeNumber;

    @Field("totalStages")
    private int totalStages;

    @Field("acidAdditives")
    private List<Chemical> acidAdditives = new ArrayList<>();

    @Field("slickwaters")
    private List<Chemical> slickwaters = new ArrayList<>();

    @Field("linearGelCrosslinks")
    private List<Chemical> linearGelCrosslinks = new ArrayList<>();

    @Field("diverters")
    private List<Chemical> diverters = new ArrayList<>();

    @Field("additionalChemicalTypes")
    private Map<String, List<Chemical>> additionalChemicalTypes = new HashMap<>();

    @Field("proppants")
    private List<Proppant> proppants = new ArrayList<>();

    @Field("inProgressChemicalStage")
    private ChemicalStageNoIndex inProgressChemicalStage = new ChemicalStageNoIndex();

    @Field("submittedChemicalStages")
    private List<ChemicalStageNoIndex> submittedChemicalStages = new ArrayList<>();

    @JsonIgnore
    private ChemicalStage tmpMigratedChemicalStage;

    @Field("inProgressProppantStage")
    private ProppantStageNoIndex inProgressProppantStage = new ProppantStageNoIndex();

    @JsonIgnore
    private ProppantStage tmpMigratedProppantStage;

    @Field("fracproId")
    private int fracproId;

    @Field("additionalFieldTicketNames")
    private List<String> additionalFieldTicketNames = new ArrayList<>();

    @Field("additionalCustomFieldTicketNames")
    private List<String> additionalCustomFieldTicketNames = new ArrayList<>();

    @Field("additionalSubStageNames")
    private List<Float> additionalSubStageNames = new ArrayList<>();

    @Field("threeDWell")
    private ThreeDWell threeDWell;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("combinedStagesAfterDiscountApplied")
    private List<String>combinedStagesAfterDiscountApplied=new ArrayList<>();

    @Field("stageInfo")
    private List<StageInfo> stageInfo =  new ArrayList<>();

    @Field("operatorId")
    @NotEmpty(message = "operator ID can not be empty")
    @Size(max = 100, message = "operator ID can not be more than 100 characters.")
    private String operatorId;

    @Field("padId")
    @NotEmpty(message = "pad ID can not be empty")
    @Size(max = 100, message = "pad ID can not be more than 100 characters.")
    private String padId;

    @Field("designRange")
    private Map<String, Set<Integer>> designRange = new HashMap<>();

    @Field("wellType")
    private String wellType;

    @Field("completionType")
    private String completionType;

    @Field("stageIntervals")
    private int stageIntervals;

    @JsonIgnore
    public ChemicalStage getTmpMigratedChemicalStage() {
        return tmpMigratedChemicalStage;
    }

    @JsonIgnore
    public ProppantStage getTmpMigratedProppantStage() {
        return tmpMigratedProppantStage;
    }

    public void setTmpMigratedProppantStages(ProppantStage tmpMigratedProppantStage) {
        this.tmpMigratedProppantStage = tmpMigratedProppantStage;
    }


}
