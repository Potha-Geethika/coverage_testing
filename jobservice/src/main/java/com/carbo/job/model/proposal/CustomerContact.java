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
public class CustomerContact {

    @Field("title")
    private String title;

    @Field("primaryEmail")
    private String primaryEmail;

    @Field("primaryPhone")
    private String primaryPhone;

    @Field("contactId")
    private String contactId;

    @Field("name")
    private String name;

}
