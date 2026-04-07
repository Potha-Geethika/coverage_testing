package com.carbo.job.model.proposal.v2;

import com.carbo.job.model.proposal.AuditDetails;
import org.springframework.data.annotation.Id;
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
@Document (collection = "proposal-design-details-v2")
public class PumpSchedule {
    @Id
    private String id;

    @Field ("stageType")
    private String stageType;

    @Field ("flowRate")
    private double flowRate;

    @Field("flowRate2")
    private double flowRate2;

    @Field ("propConc")
    private double propConc;

    @Field("propConc2")
    private double propConc2;

    @Field("stageMode")
    private String stageMode;

    @Field ("cleanVolume")
    private double cleanVolume;

    @Field ("stageLength")
    private double stageLength;

    @Field ("cumulitiveTime")
    private double cumulitiveTime;

    @Field ("fluidType")
    private String fluidType;

    @Field ("proppantType")
    private String proppantType;

    @Field ("stageProp")
    private double stageProp;

    @Field ("slurryVolume")
    private double slurryVolume;

    @Field ("organizationId")
    private String organizationId;

    @Field ("designId")
    private String designId;

    @Field ("proposalId")
    private String proposalId;

    @Field ("auditDetails")
    private AuditDetails auditDetails;

}
