package com.carbo.job.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(
        collection = "checklists"
)
public class CheckList {
    @Id
    private String id;
    @Field("jobId")
    private @NotEmpty(
            message = "job id can not be empty"
    ) String jobId;
    @Field("day")
    private @NotEmpty(
            message = "day can not be empty"
    ) Integer day;
    @Field("shift")
    private @NotEmpty(
            message = "shift can not be empty"
    ) String shift;
    @Field("items")
    private List<CheckItem> items = new ArrayList();
    @Field("locked")
    private Boolean locked;
    @Field("modified")
    private Long modified = (new Date()).getTime();
    @Field("organizationId")
    private String organizationId;
    @Field("ts")
    private Long ts;
    @Field("lastModifiedBy")
    private String lastModifiedBy;

    public CheckList() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getShift() {
        return this.shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getDay() {
        return this.day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public List<CheckItem> getItems() {
        return this.items;
    }

    public void setItems(List<CheckItem> items) {
        this.items = items;
    }

    public Boolean getLocked() {
        return this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
