package com.carbo.job.events.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceBookComponentNameChangeModel implements Serializable {

    private String organizationId;
    private String priceBookId;
    private String componentId;
    private String oldName;
    private String newName;
    private ComponentType type;
    private String itemCode;
    private Long timestamp;

    public enum ComponentType {
        CHEMICAL, PROPPANT
    }
}
