package com.carbo.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCodeDetails {

    private Map<String, Double> itemCodeMap;

    private DiscountAuditDetails discountAuditDetails = new DiscountAuditDetails();

}
