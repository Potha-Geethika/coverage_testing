package com.carbo.job.model.proposal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreparedBy {

    @Field("title")
    private String title;

    @Field("primaryEmail")
    private String primaryEmail;

    @Field("salesUserId")
    private String salesUserId;

    @Field("name")
    private String name;

}
