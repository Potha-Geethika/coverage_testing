package com.carbo.job.model.widget.misc;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Document(collection = "chemicals")
@CompoundIndexes(
        {
                @CompoundIndex(def = "{'organizationId': 1, 'name': 1}", name = "organization_name_index", unique = true),
                @CompoundIndex(def = "{'organizationId': 1, 'code': 1}", name = "organization_code_index", unique = true)
        }
)
public class Chemical {
    @Id
    private String id;

    @Field("name")
    @NotEmpty(message = "name can not be empty")
    private String name;

    @Field("subtype")
    @NotEmpty(message = "subtype can not be empty")
    private String subtype;

    @Field("types")
    @NotEmpty(message = "types can not be empty")
    private List<Integer> types;

    @Field("code")
    private String code;

    @Field("price")
    private Float price;

    @Field("uom")
    private String uom;

    @Field("accountGroup")
    private String accountGroup;

    @Field("acidName")
    private String acidName;

    @Field("acidDilutionRate")
    private String acidDilutionRate;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("organizationId")
    private String organizationId;

    @Field("isCustomerSupplied")
    private Boolean isCustomerSupplied;

    @Field("calculateByStraps")
    private Boolean calculateByStraps;

    @Field("jobId")
    private String jobId;


    @Field("wellId")
    private String wellId;

    @Field("wellName")
    private String wellName;

    public String getWellId() {
        return wellId;
    }

    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    public String getWellName() {
        return wellName;
    }

    public void setWellName(String wellName) {
        this.wellName = wellName;
    }


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }


    public Boolean getCalculateByStraps() {
        return calculateByStraps;
    }

    public void setCalculateByStraps(Boolean calculateByStraps) {
        this.calculateByStraps = calculateByStraps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public Boolean getIsCustomerSupplied() {
        return isCustomerSupplied;
    }

    public void setIsCustomerSupplied(Boolean isCustomerSupplied) {
        this.isCustomerSupplied = isCustomerSupplied;
    }

    public String getAcidName() {
        return acidName;
    }

    public void setAcidName(String acidName) {
        this.acidName = acidName;
    }

    public String getAcidDilutionRate() {
        return acidDilutionRate;
    }

    public void setAcidDilutionRate(String acidDilutionRate) {
        this.acidDilutionRate = acidDilutionRate;
    }
}
