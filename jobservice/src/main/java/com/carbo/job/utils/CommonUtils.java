package com.carbo.job.utils;

import com.carbo.job.model.widget.AuditDetails;

import jakarta.servlet.http.HttpServletRequest;

import static com.carbo.job.utils.ControllerUtil.getUserName;

public class CommonUtils {
    public static double roundValue(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public static AuditDetails setAuditDetails(HttpServletRequest request, AuditDetails auditDetails) {
        // Check if the provided auditDetails object is null.
        if (auditDetails == null) {
            // If it is null, create a new AuditDetails object and set the created time and created by.
            auditDetails = new AuditDetails();
            auditDetails.setCreatedTime(System.currentTimeMillis() / 1000);
            auditDetails.setCreatedBy(getUserName(request));
        }
        // Set the modified time and modified by with the current timestamp and the authenticated user's name.
        auditDetails.setModifiedTime(System.currentTimeMillis() / 1000);
        auditDetails.setModifiedBy(getUserName(request));
        // Return the updated AuditDetails object with the audit details set.
        return auditDetails;
    }

    public static Double round(Double number, int decimalPlaces) {
        double pow = Math.pow(10, decimalPlaces);
        return Math.round(number * pow) / pow;
    }

}