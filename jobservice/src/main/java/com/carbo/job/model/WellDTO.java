package com.carbo.job.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WellDTO {

    @Id
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("api")
    private String api;

    @JsonProperty("afeNumber")
    private String afeNumber;

    @JsonProperty("totalStages")
    private int totalStages;

    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("proppants")
    private List<Proppant> proppants;

}
