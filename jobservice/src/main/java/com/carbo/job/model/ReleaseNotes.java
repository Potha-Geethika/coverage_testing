package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseNotes {
    private String itemId;
    private String description;

    private TypeReleaseNote type;


}