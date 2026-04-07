package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "photos")
public class Photo {
    @Id
    private String id;

    @Field("uri")
    private String uri;

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("ts")
    private Long ts;

    @Field("organizationId")
    private String organizationId;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }


}
