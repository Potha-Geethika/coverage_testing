package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Document(collection = "field-tickets")
@CompoundIndex (def = "{'jobId': 1, 'well': 1, 'name': 1, 'organizationId': 1}", name = "jobId_well_name_organizationId_index", unique = true)
public class FieldTicket {
    @Id
    private String id;

    @Field("date")
    private Date date;

    @Field("jobId")
    private String jobId;

    @Field("well")
    private String well;

    @Field("name")
    private String name;

    @Field("versions")
    private List<FieldTicketVersion> versions = new ArrayList<>();

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<FieldTicketVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<FieldTicketVersion> versions) {
        this.versions = versions;
    }

    public Long getCreated() {
        return created;
    }

    public Optional<FieldTicketVersion> getLastVersion() {
        if (versions.isEmpty()) {
            return Optional.empty();
        }
        else {
            FieldTicketVersion lastVersion = versions.get(versions.size() - 1);
            return Optional.of(lastVersion);
        }
    }
}
