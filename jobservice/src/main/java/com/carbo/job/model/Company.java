package com.carbo.job.model;

import com.carbo.job.model.widget.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "companies")
@CompoundIndex(name = "unique_companyid_organizationid_index", def = "{'companyId': 1, 'organizationId': 1}", unique = true)
@CompoundIndex(name = "unique_companyname_organizationid_index", def = "{'companyName': 1, 'organizationId': 1}", unique = true)
public class Company {

    @Field("companyId")
    private String companyId;

    @NotEmpty(message = "companyName can not be empty")
    @Field("companyName")
    private String companyName;

    @Field("auditDetails")
    private AuditDetails auditDetails;

    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    //Sequence uniqueCompanyId
    @Field("uniqueCompanyId")
    private String uniqueCompanyId;

    public Company(String companyName){
        this.companyName = companyName;
    }

}

