package com.carbo.job.model.widget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document (collection = "chemical-material-needed")
@CompoundIndex (name = "unique_jobId_index", def = "{'jobId': 1}", unique = true, sparse = true)
public class MaterialNeeded {
    @Id
    private String id;

    @Field ("jobId")
    private String jobId;

    @Field ("materialData")
    private Set<MaterialData> materialData;

    @Field("organizationId")
    private String organizationId;

    @Field("auditDetails")
    private AuditDetails auditDetails;

}
