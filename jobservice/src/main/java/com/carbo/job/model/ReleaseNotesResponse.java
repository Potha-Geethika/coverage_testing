package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(def = "{'date': 1}", name = "date_index", unique = true)

@Document(collection = "release-notes")
public class ReleaseNotesResponse {
    @Field("date")
    public String date;
    @Id
    private String id;
    @Field("notes")
    private Map<String, String> notes;

    @Field("created")
    private long created = new Date().getTime();

    @Field("modified")
    private long modified = new Date().getTime();

    @Field("createdBy")
    private String createdBy;

    @Field("modifiedBy")
    private String modifiedBy;
}
