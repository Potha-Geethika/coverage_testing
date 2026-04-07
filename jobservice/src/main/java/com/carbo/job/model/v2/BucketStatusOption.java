package com.carbo.job.model.v2;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "bucket_test_statuses")
public class BucketStatusOption {

    @Id
    private String id;

    @Field("statusName")
    private String statusName;

    @Field("organizationId")
    private String organizationId;

    @Field("isDefault")
    private boolean isDefault;

    @Field("createdTime")
    private Long createdTime;

    @Field("createdBy")
    private String createdBy;
}
